package com.arde.media.rmp;

import java.util.List;
import java.util.Observer;

import com.arde.media.common.Song;
import com.arde.media.rmp.impl.PlayListStatus;

public interface IPlaylistManager {
	public static enum PlaylistEvent {
		PLAYLIST_CHANGED, NOWPLAYING_CHANGED
	}
	/**
	 * Add a song to the play queue
	 * @param s
	 */
	public void addSong(Song s);
	
	/**
	 * Remove song from queue
	 * @param s - the Song to remove
	 */
	public void removeSong(Song s);

	/**
	 * Remove song from queue
	 * @param key - the key of the song to remove
	 */
	public void removeSong(String key);
	
	/**
	 * Stop playing the current song
	 */
	public void stopPlaying();
	
	/**
	 * Move to next song
	 */
	public void skip();
	
	/**
	 * Return currently playing song
	 * @return
	 */
	public Song getNowPlaying();
	
	public void pauseOrResume();
	
	public List<Song> getPlayList();

	public PlayListStatus getPlayListStatus();
}
