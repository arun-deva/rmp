package com.arde.media.musicsource.search;

import java.util.concurrent.Future;

import com.arde.media.common.MusicSource;

public interface IMediaIndexer {
	public Future<MusicSourceIndexed> indexMusicSource(MusicSource musicSource);
	public void setSelectedMusicSource(MusicSource musicSource);
	public MusicSource getSelectedMusicSource();
}
