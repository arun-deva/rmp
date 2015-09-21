package com.arde.media.rmp.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.arde.media.common.IMediaPlayer;
import com.arde.media.common.Song;
import com.arde.media.common.IMediaPlayer.PlayerCommand;
import com.arde.media.common.IMediaPlayer.PlayerEventType;
import com.arde.media.rmp.IPlayList;
import com.arde.media.rmp.IPlaylistManager;
import com.arde.media.rmp.MediaPlayerQualifier;
import com.arde.media.rmp.PlayerEventQualifier;

@ApplicationScoped
public class PlayListManager implements IPlaylistManager {
	@Inject @MediaPlayerQualifier("Java")
	IMediaPlayer playerBean;
	
	@Inject
	IPlayList playList;
	
	Logger logger = Logger.getLogger(this.getClass().getName());
	private PlayListStatus playListStatus = new PlayListStatus();

	public void handlePlayStarted(@Observes @PlayerEventQualifier(type=PlayerEventType.PLAY_STARTED) Song song) {
		//both now playing and play list have changed (since now playing would have been moved out of playlist)
		logger.info("Play list manager play started for song: " + song);
		playlistChanged();
		songChanged(song);
	}
	
	@Override
	public void addSong(Song s) {
		playList.addSong(s);
		playlistChanged();
		playerBean.doCommand(PlayerCommand.PLAY); //player will play if it is not already playing
	}
	
	@Override
	public void stopPlaying() {
		logger.info("Stopping Play for " + playListStatus.getNowPlayingSong());
		playerBean.doCommand(PlayerCommand.STOP);
		songChanged(null);
	}
	@Override
	public void removeSong(Song s) {
		logger.info("Removing song " + s);
		playList.removeSong(s);
		playlistChanged();
	}
	
	public void removeSong(String key) {	
		logger.info("Removing song with key " + key);
		playList.removeSong(key);
		playlistChanged();
	}
	
	@Override
	public void skip() {
		logger.info("Skipping to next song - current is " + playListStatus.getNowPlayingSong());
		playerBean.doCommand(PlayerCommand.SKIP);
		logger.info("Skipped to next song - current is " +  playListStatus.getNowPlayingSong());
	}
	
	public void pauseOrResume() {
		playerBean.doCommand(PlayerCommand.PAUSE_OR_RESUME);
	}
	
	@Override
	public Song getNowPlaying() {
		return playListStatus.getNowPlayingSong();
	}

	private void songChanged(Song song) {
		playListStatus.setNowPlayingChangedMillis(System.currentTimeMillis());
		playListStatus.setNowPlayingSong(song);
	}
	private void playlistChanged() {
		playListStatus.setPlayListChangedMillis(System.currentTimeMillis());
	}

	@Override
	public List<Song> getPlayList() {
		return playList.getList();
	}
	
	@Override
	public PlayListStatus getPlayListStatus() {
		return playListStatus;
	}
}
