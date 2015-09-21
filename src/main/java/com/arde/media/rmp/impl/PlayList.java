package com.arde.media.rmp.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.arde.media.common.IMediaQueue;
import com.arde.media.common.Song;
import com.arde.media.rmp.IPlayList;
import com.arde.media.rmp.MetadataChangedEventQualifier;

@ApplicationScoped
public class PlayList implements IMediaQueue, IPlayList {
	private LinkedList<Song> playList = new LinkedList<Song>();
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public Song getNextSongToPlay() {
		Song nextSong = playList.poll();
		if (nextSong != null) {
			logger.info("Retrieved next song to play: " + nextSong);
			return nextSong;
		}
		return null;
	}

	@Override
	public synchronized void addSong(Song s) {
		logger.info("Adding song to playlist: " + s);
		playList.add(s);
	}

	@Override
	public synchronized void removeSong(Song s) {
		playList.remove(s);
	}

	@Override
	public void removeSong(String key) {
		for (Song s : playList) {
			if (s.getKey().equals(key)) {
				removeSong(s);
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
}
