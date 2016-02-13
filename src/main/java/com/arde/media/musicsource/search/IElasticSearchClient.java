package com.arde.media.musicsource.search;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IElasticSearchClient {
	public void bulkIndexDocument(String index, String type, String id, byte[] jsonDocument);

	public void startBulkIndexing();

	public void stopBulkIndexing();

	public void removeIndex(String index);

	<T> List<T> search(String indexName, String typeName, String query, Class<T> resultClass) throws IOException;

	<T> T lookupByKey(String indexName, String typeName, String key, Class<T> resultClass) throws IOException;

	<T> List<T> random(String indexName, String typeName, int howMany,
			Class<T> resultClass);

	public <T> void index(String indexName, String typeName, Indexable<T> indexableEntity)
			throws JsonProcessingException;

	public long getCount(String indexName, String typeName);

	public <T> List<T> getAll(String indexName, String typeName, Class<T> resultClass);
}