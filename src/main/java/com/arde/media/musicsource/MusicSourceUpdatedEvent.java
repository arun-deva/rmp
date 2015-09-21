package com.arde.media.musicsource;

public class MusicSourceUpdatedEvent {
	private MusicSource musicSource;

	public MusicSourceUpdatedEvent(MusicSource ms) {
		this.musicSource = ms;
	}
	public MusicSource getMusicSource() {
		return musicSource;
	}

}
