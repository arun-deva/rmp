package com.arde.media.common;

public class MusicSource {
	private String location = null;
	private boolean ready;
    private long numSongs;
    
	public MusicSource() {
		
	}
	public MusicSource(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * @return the ready
	 */
	public boolean isReady() {
		return ready;
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public long getNumSongs() {
		return numSongs;
	}
	public void setNumSongs(long numSongs) {
		this.numSongs = numSongs;
	}

}
