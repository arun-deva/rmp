package com.arde.media.rmp.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.arde.media.common.IMediaPlayer;
import com.arde.media.common.IPlayList;
import com.arde.media.common.Song;
import com.arde.media.common.IMediaPlayer.PlayerCommand;
import com.arde.media.common.IMediaPlayer.PlayerEventType;
import com.arde.media.rmp.IPlaylistManager;
import com.arde.media.rmp.MediaPlayerQualifier;
import com.arde.media.rmp.PlayerEventQualifier;
import com.arde.media.rmp.RmpSettings;

@ApplicationScoped
public class PlayListManager implements IPlaylistManager {
	@Inject @MediaPlayerQualifier(RmpSettings.DEFAULT_MEDIA_PLAYER)
	IMediaPlayer playerBean;

	PlayList playList;
	
	Logger logger = Logger.getLogger(this.getClass().getName());

	@PostConstruct
	public void initialize() {
		//instantiate the playlist instead of injecting it
		//2 reasons - (1) it is in same package, no need to inject
		//(2) This helps eliminate the need for an @Resource injection of a managed thread factory in JavaMediaPlayer, which doesn't
		//work in Jetty
		playList = new PlayList();
		playerBean.setPlayList(playList);
	}
	
	@Override
	public void addSong(Song s) {
		playList.addSong(s);
		playerBean.doCommand(PlayerCommand.PLAY); //player will play if it is not already playing
	}
	
	@Override
	public void stopPlaying() {
		logger.info("Stopping Play for " + playList.getStatus().getNowPlayingSong());
		playerBean.doCommand(PlayerCommand.STOP);
	}
	@Override
	public void removeSong(Song s) {
		logger.info("Removing song " + s);
		playList.removeSong(s);
	}
	
	public void removeSong(String key) {	
		logger.info("Removing song with key " + key);
		playList.removeSong(key);
	}
	
	@Override
	public void skip() {
		logger.info("Skipping to next song - current is " + playList.getStatus().getNowPlayingSong());
		playerBean.doCommand(PlayerCommand.SKIP);
		logger.info("Skipped to next song - current is " +  playList.getStatus().getNowPlayingSong());
	}
	
	public void pauseOrResume() {
		playerBean.doCommand(PlayerCommand.PAUSE_OR_RESUME);
	}

	@Override
	public List<Song> getPlayList() {
		return playList.getList();
	}
	
	@Override
	public PlayListStatus getPlayListStatus() {
		return playList.getStatus();
	}

	@Override
	public Song getNowPlaying() {
		return playList.getNowPlaying();
	}
}
