package com.arde.media.musicsource;

import java.util.ArrayList;
import java.util.List;

public class MusicSourceInfo {
	private MusicSource currentMusicSource;
	private List<MusicSource> potentialMusicSources = new ArrayList<MusicSource>();
	/**
	 * @return the currentMusicSource
	 */
	public MusicSource getCurrentMusicSource() {
		return currentMusicSource;
	}

	/**
	 * @return the potentialMusicSources
	 */
	public List<MusicSource> getPotentialMusicSources() {
		return potentialMusicSources;
	}

	public MusicSourceInfo(MusicSource currentMusicSource,
			List<MusicSource> potentialMusicSources) {
		super();
		this.currentMusicSource = currentMusicSource;
		this.potentialMusicSources = potentialMusicSources;
	}
}
