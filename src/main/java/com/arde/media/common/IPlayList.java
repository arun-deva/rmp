package com.arde.media.common;

import java.util.List;

public interface IPlayList {

	public void addSong(Song s);

	public void removeSong(Song s);

	public void removeSong(String key);

	public List<Song> getList();

	public Song getNowPlaying();
	
	/**
	 * Expected to block until file available
	 * @return
	 */
	public Song getNextSongToPlay();

	public void songChanged(Song song);


}
