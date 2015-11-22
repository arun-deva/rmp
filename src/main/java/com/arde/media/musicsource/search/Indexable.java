package com.arde.media.musicsource.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface Indexable<T> {
	public String getKey();
	
	public T getEntity();
	
	default public byte[] getJsonDocument() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(getEntity());
	}
}
