package com.arde.media.rmp;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

@ApplicationScoped
public class ElasticsearchNode {
	private static final String NODE_NAME = System.getProperty("ES_NODE_NAME", "rmpEs");
	private static final String DATA_PATH = System.getProperty("ES_DATA_PATH", System.getProperty("user.home") + File.separatorChar + "rmpEsData");
	private static final String CLUSTER_NAME = System.getProperty("ES_CLUSTER_NAME", "rmpEsCluster");
	
	private Node esNode;
	
	@PostConstruct
	private void createEsNode() {
		File dataDir = new File(DATA_PATH);
		if (!dataDir.isDirectory())	dataDir.mkdirs();
		
		ImmutableSettings.Builder settingsBuilder =
				ImmutableSettings.settingsBuilder();

		settingsBuilder.put("node.name", NODE_NAME);
		settingsBuilder.put("path.data", DATA_PATH);
		settingsBuilder.put("http.enabled", false);

		Settings settings = settingsBuilder.build();

		esNode = NodeBuilder.nodeBuilder()
				.settings(settings)
				.clusterName(CLUSTER_NAME)
				.data(true).local(true).node();	
	}

	public Client getClient() {
		return esNode.client();
	}
	
	@PreDestroy
	private void shutdownEsNode() {
		if (esNode != null) {
			esNode.close();
		}
	}
}
