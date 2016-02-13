package com.arde.media.musicsource.search.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;

import com.arde.media.musicsource.search.IElasticSearchClient;
import com.arde.media.musicsource.search.Indexable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class ElasticSearchClient implements IElasticSearchClient {
	private Client esClient;

	private Node node;
	private BulkProcessor bulkProcessor;
	private static final Logger LOGGER = Logger.getLogger(ElasticSearchClient.class.getName());
	@Override
	public void bulkIndexDocument(String index, String type, String id,
			byte[] jsonDocument) {
		if (bulkProcessor == null) {
			throw new IllegalStateException("Cannot bulk index document when a bulk indexing operation has not been started!");
		}

		bulkProcessor.add(new IndexRequest(index, type, id).source(jsonDocument));
	}
	
	@Override
	public <T> T lookupByKey(String indexName, String typeName, String key, Class<T> resultClass) throws IOException {
		GetResponse resp = esClient.prepareGet(indexName, typeName, key).execute().actionGet();
		if (!resp.isExists() || resp.isSourceEmpty()) return null;
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(resp.getSourceAsBytes(), resultClass);
	}
	
	@Override
	public <T> List<T> random(String indexName, String typeName, int howMany, Class<T> resultClass) {
		//create a scoring function with random mode seeded by current millis
		ScoreFunctionBuilder randomScoreFunctionBuilder = ScoreFunctionBuilders.randomFunction(System.currentTimeMillis());
		
		SearchResponse resp = esClient.prepareSearch(indexName)
				.setTypes(typeName).
				setSize(howMany).
				setQuery(QueryBuilders.functionScoreQuery().add(randomScoreFunctionBuilder))
				.execute()
				.actionGet();
		return mapToResultClass(resultClass, resp.getHits().getHits());
	}
	
	@Override
	public <T> void index(String indexName, String typeName, Indexable<T> indexableEntity) throws JsonProcessingException {
		esClient.index(new IndexRequest(indexName, typeName, indexableEntity.getKey()).source(indexableEntity.getJsonDocument()));
	}
	
	@Override
	public void removeIndex(String index) {
		boolean exists = esClient.admin().indices().prepareExists(index).execute().actionGet().isExists();
		if (exists) {
			esClient.admin().indices().delete(new DeleteIndexRequest(index)).actionGet();
		}
	}

	@Override
	public <T> List<T> search(String indexName, String typeName, String query, Class<T> resultClass) throws IOException {
		//Use a QueryStringQuery which will match "_all" i.e. all fields by default
		SearchResponse resp = esClient.prepareSearch(indexName)
				.setTypes(typeName).setQuery(QueryBuilders.queryStringQuery(query))
				.execute().actionGet();
		return mapToResultClass(resultClass, resp.getHits().getHits());
	}

	@Override
	public synchronized void startBulkIndexing() {
		if (bulkProcessor != null) {
			throw new IllegalStateException("Bulk indexing operation cannot be started when another bulk indexing is already in progress!");
		}
		bulkProcessor = BulkProcessor.builder(
		        esClient,  
		        new BulkProcessor.Listener() {
		            @Override
		            public void afterBulk(long executionId,
		                                  BulkRequest request,
		                                  BulkResponse response) { 
		            	System.out.println("BulkProcessor afterBulk, hasFailures = " + response.hasFailures());
		            } 

		            @Override
		            public void afterBulk(long executionId,
		                                  BulkRequest request,
		                                  Throwable failure) {
		            	System.out.println("BulkProcessor afterBulk, throwable = " + failure);
		            } 

		            @Override
		            public void beforeBulk(long executionId,
		                                   BulkRequest request) {  } 
		        })
		        .setBulkActions(100) 
		        .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.MB)) 
		        .setFlushInterval(TimeValue.timeValueSeconds(5)) 
		        .setConcurrentRequests(1) 
		        .build();
	}

	@Override
	public void stopBulkIndexing() {
		LOGGER.info("stopBulkIndexing");
		bulkProcessor.close();
		bulkProcessor = null;
	}

	@PostConstruct
	private void initializeESClient() {
		node = NodeBuilder.nodeBuilder()
				.settings(ImmutableSettings.settingsBuilder().put("http.enabled", false)) //ES should not open http port for this client node to listen on
				.client(true)
				.node();
		esClient = node.client();
	}

	private <T> List<T> mapToResultClass(Class<T> resultClass, SearchHit[] hits) {
		Function<byte[], T> bytesToResultMapper = new ByteArrayToResultMapper<T>(resultClass);
		return Arrays.stream(hits)
				.map(SearchHit::source) //map each search hit to the byte[] returned by its source() method
				.map(bytesToResultMapper)
				.filter(b -> b != null)
				.collect(Collectors.toList());
	}
	
	@PreDestroy
	private void shutdownESClientNode() {
		if (node != null) {
			node.close();
		}
	}
	
	private static final class ByteArrayToResultMapper<T> implements
			Function<byte[], T> {
		private Class<T> resultClass;
		
		ObjectMapper mapper = new ObjectMapper();

		public ByteArrayToResultMapper(Class<T> resultClass) {
			this.resultClass = resultClass;
		}

		@Override
		public T apply(byte[] bytes) {
			try {
				return mapper.readValue(bytes, resultClass);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, String.format("Failed to map bytes to %s", resultClass.getName()), e);
				return null;
			}
		}
	}

	@Override
	public long getCount(String indexName, String typeName) {
		CountResponse resp = esClient.prepareCount(indexName).setTypes(typeName).execute().actionGet();
		return resp.getCount();
	}

	@Override
	public <T> List<T> getAll(String indexName, String typeName,
			Class<T> resultClass) {
		SearchResponse response = esClient.prepareSearch(indexName).setTypes(typeName)
				.setQuery(QueryBuilders.queryStringQuery(("*:*")))
				.execute().actionGet();
		return mapToResultClass(resultClass, response.getHits().getHits());
	}
	

}
