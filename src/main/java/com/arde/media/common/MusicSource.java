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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MusicSource other = (MusicSource) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}

}
