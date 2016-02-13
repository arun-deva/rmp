package com.arde.media.musicsource;

import java.io.IOException;
import java.util.List;

import com.arde.media.common.MusicSource;
import com.arde.media.common.MusicSourceInfo;

public interface IMusicSourceManager {

	public abstract MusicSource getMusicSource();

	public abstract List<MusicSource> getRoots() throws IOException;

	public abstract MusicSourceInfo getMusicSourceInfo() throws IOException;

	public abstract void updateMusicSource(MusicSource musicSource);

	public void waitForMusicSourceReady();

	public void reIndexMusicSource();

}