package com.arde.media.rmp.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.arde.media.common.IPlayList;
import com.arde.media.common.Song;
import com.arde.media.rmp.MetadataChangedEventQualifier;

@ApplicationScoped
public class PlayList implements IPlayList {
	private LinkedList<Song> playList = new LinkedList<Song>();
	Logger logger = Logger.getLogger(this.getClass().getName());
	private PlayListStatus playListStatus = new PlayListStatus();
	
	@Override
	public Song getNextSongToPlay() {
		Song nextSong = playList.poll();
		if (nextSong != null) {
			logger.info("Retrieved next song to play: " + nextSong);
			playlistChanged();
			return nextSong;
		}
		return null;
	}

	@Override
	public synchronized void addSong(Song s) {
		logger.info("Adding song to playlist: " + s);
		playList.add(s);
		playlistChanged();
	}

	@Override
	public synchronized void removeSong(Song s) {
		playList.remove(s);
		playlistChanged();
	}

	@Override
	public void removeSong(String key) {
		for (Song s : playList) {
			if (s.getKey().equals(key)) {
				removeSong(s);
				playlistChanged();
				return;
			}
		}
	}

	@Override
	public List<Song> getList() {
		return playList;
	}

	public void songMetaDataUpdated(@Observes @MetadataChangedEventQualifier Song s) {
		int idx = playList.indexOf(s);
		if (idx < 0) return;
		logger.info("songMetaDataUpdated - updating song info for " + s);
		playList.get(idx).setSongInfo(s.getSongInfo());
	}
	
	@Override
	public Song getNowPlaying() {
		return playListStatus.getNowPlayingSong();
	}

	@Override
	public void songChanged(Song song) {
		playListStatus.setNowPlayingChangedMillis(System.currentTimeMillis());
		playListStatus.setNowPlayingSong(song);
	}
	private void playlistChanged() {
		playListStatus.setPlayListChangedMillis(System.currentTimeMillis());
	}

	public PlayListStatus getStatus() {
		return playListStatus;
	}
}
