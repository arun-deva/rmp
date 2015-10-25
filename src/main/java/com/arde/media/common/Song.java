package com.arde.media.common;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true) //to ignore filename during deserialization
public class Song {
	private SongInfo songInfo;
	private String key;
	
	//empty constructor for Jackson to use
	public Song() {
		
	}
	public Song(File f) {
		key = f.getAbsolutePath();
	}

	public Song(String key) {
		if (!isKeyValid(key)) {
			throw new IllegalArgumentException("Invalid key for song:" + key);
		}
		this.key = key;
	}
	public String getKey() {
		return key;
	}

	
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	public File getFile() {
		return new File(key);
	}

	public String getFileName() {
		return getFile().getName();
	}
	
	
	private boolean isKeyValid(String key) {
		return new File(key).canRead();
	}
	
	public boolean equals(Object o) {
		Song s = (Song) o;
		return (s != null && s.getKey().equals(this.getKey()));
	}

	public void setSongInfo(SongInfo songInfo) {
		this.songInfo = songInfo;
	}

	public SongInfo getSongInfo() {
		return songInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Song [file=" + getFileName() + "]";
	}
}
