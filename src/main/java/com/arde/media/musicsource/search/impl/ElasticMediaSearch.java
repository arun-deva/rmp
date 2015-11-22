package com.arde.media.musicsource.search.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.arde.media.common.Song;
import com.arde.media.common.exceptions.MusicSourceNotSetException;
import com.arde.media.musicsource.IMusicSourceManager;
import com.arde.media.musicsource.search.ElasticSearchConstants;
import com.arde.media.musicsource.search.IElasticSearchClient;
import com.arde.media.musicsource.search.IMediaSearch;
import com.arde.media.musicsource.search.MediaSearchQualifier;
import com.arde.media.musicsource.search.MediaSearchType;

@ApplicationScoped
@MediaSearchQualifier(MediaSearchType.ELASTICSEARCH)
public class ElasticMediaSearch implements IMediaSearch {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	@Inject
	IElasticSearchClient esClient;

	@Inject
	IMusicSourceManager musicSrcMgr;
	
	@Override
	public List<Song> findSongs(String searchStr) throws IOException {
		musicSrcMgr.waitForMusicSourceReady();
		return esClient.search(ElasticSearchConstants.RMP_INDEX_NAME, ElasticSearchConstants.SONG_TYPE_NAME, searchStr, Song.class);
	}

	@Override
	public Song findSongByKey(String key) throws IOException {
		musicSrcMgr.waitForMusicSourceReady();
		return esClient.lookupByKey(ElasticSearchConstants.RMP_INDEX_NAME, ElasticSearchConstants.SONG_TYPE_NAME, key, Song.class);
	}

	@Override
	public Collection<Song> findRandomSongs(int howMany) {
		musicSrcMgr.waitForMusicSourceReady();
		return esClient.random(ElasticSearchConstants.RMP_INDEX_NAME, ElasticSearchConstants.SONG_TYPE_NAME, howMany, Song.class);
	}

}
