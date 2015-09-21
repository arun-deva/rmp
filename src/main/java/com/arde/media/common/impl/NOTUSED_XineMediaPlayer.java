package com.arde.media.common.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.Observer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.libxinejna.Xine;
import org.libxinejna.XineController;

import com.arde.media.common.IMediaPlayer;
import com.arde.media.common.IMediaPlayer.PlayerState;
import com.arde.media.rmp.MediaPlayerQualifier;
import com.arde.media.rmp.impl.PlayerEventQualifierFactory;

@ApplicationScoped
@MediaPlayerQualifier("Xine")
public class NOTUSED_XineMediaPlayer implements IMediaPlayer {
	XineController controller;
	@Inject
	private PlayerEventQualifierFactory playerEventQualifierFactory;
	
	private PlayerState playerState = PlayerState.IDLE;
	
	public void play() {
		final File myMediaFile = new File(""); //DUMMY - need to rewrite this method to play all files in media queue (see JavaMediaPlayer)
		
        /*XineAWTCanvas view = new XineAWTCanvas();
        Frame awtFrame = new Frame("MuXine Java Video Player");
        awtFrame.add(view);
        awtFrame.setVisible(true);
        awtFrame.setSize(320, 240);
        awtFrame.setBackground(Color.BLACK);*/
		Thread playerThread = new Thread() {

			@Override
			public void run() {
				doPlay(myMediaFile);
			}

		};
		playerThread.start();
		setPlayerState(PlayerState.PLAYING);
	}

	private void doPlay(File mediaFile) {
        Xine engine = Xine.getXine();
		controller = engine.createController();
		controller.open(mediaFile.getAbsolutePath());
		controller.playOnce();
		notifyPlayerEvent(PlayerEventType.PLAY_STARTED);
	}

	private boolean isPlaying() {
		if (controller != null) return controller.isPlaying();
		return false;
	}
	
	public void stop() {
		if (controller != null) {
			setPlayerState(PlayerState.STOPPED);
			controller.stop();
			notifyPlayerEvent(PlayerEventType.PLAY_STOPPED);
		}
	}
	
	public void pauseOrResume() {
		if (controller != null) {
			if (controller.isPaused()) {
				setPlayerState(PlayerState.PLAYING);
				controller.resume();
			}
			else {
				setPlayerState(PlayerState.PAUSED);
				controller.pause();
			}
//			notifyPlayerEvent(PlayerEvent.PLAY_STOPPED);
		}
	}
	
	private void notifyPlayerEvent(PlayerEventType event) {
		playerEventQualifierFactory.getQualifiedPlayerEvent(event).fire(null);
	}
	
	@Override
	public FileFilter getSupportedFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				return true;
			}
			
		};
	}	
	
	@Override
	public PlayerState getPlayerState() {
		return playerState ;
	}	

	private void setPlayerState(PlayerState state) {
		playerState = state;
	}

	public void skip() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doCommand(PlayerCommand command) {
		// TODO Auto-generated method stub
		
	}
}
