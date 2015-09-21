package com.arde.media.rmp.impl;

import com.arde.media.common.Song;

public class PlayListStatus {
	private long nowPlayingChangedMillis;
	private long playListChangedMillis;
	private Song nowPlayingSong;
	
	public long getNowPlayingChangedMillis() {
		return nowPlayingChangedMillis;
	}
	public void setNowPlayingChangedMillis(long nowPlayingChangedMillis) {
		this.nowPlayingChangedMillis = nowPlayingChangedMillis;
	}
	public long getPlayListChangedMillis() {
		return playListChangedMillis;
	}
	public void setPlayListChangedMillis(long playListChangedMillis) {
		this.playListChangedMillis = playListChangedMillis;
	}
	public Song getNowPlayingSong() {
		return nowPlayingSong;
	}
	public void setNowPlayingSong(Song nowPlayingSong) {
		this.nowPlayingSong = nowPlayingSong;
	}

}
