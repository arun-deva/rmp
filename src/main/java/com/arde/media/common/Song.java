package com.arde.media.common;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true) //to ignore filename during deserialization
public class Song {
	@JsonIgnore
	//Don't unmarshall file
	private File file;
	private String key;
	private SongInfo songInfo;
	
	//empty constructor for Jackson to use
	public Song() {
		
	}
	public Song(String key, File f) {
		this.key = key;
		this.file = f;
	}

	public String getKey() {
		return key;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return file.getName();
	}
	
	public boolean equals(Object o) {
		Song s = (Song) o;
		return (s != null && s.getKey().equals(key));
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
		return "Song [file=" + file.getName() + "]";
	}
}
