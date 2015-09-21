package com.arde.media.musicsource;

import java.util.ArrayList;
import java.util.List;

public class MusicSource {
	private String location = null;
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
}
