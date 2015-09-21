package com.arde.media.common.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Observer;

import javax.enterprise.context.ApplicationScoped;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Time;
import javax.sound.sampled.AudioSystem;

import com.arde.media.common.IMediaPlayer;
import com.arde.media.rmp.MediaPlayerQualifier;

@ApplicationScoped
@MediaPlayerQualifier("JMF")
public class NOTUSED_JMFMediaPlayer implements IMediaPlayer {
	Player jPlayer;
	Time pausedMediaTime = null;

	public boolean isPlaying() {
		return (jPlayer.getState() == Player.Started);
	}

	public void pauseOrResume() {
		if (isPlaying()) {
			//need to pause it
			pausedMediaTime = jPlayer.getMediaTime();
			jPlayer.stop();
		}
		else {
			//need to resume
			if (pausedMediaTime != null) {
				jPlayer.setMediaTime(pausedMediaTime);
			}
			jPlayer.start();
		}
			
	}

	public void play() {
		
		final File myMediaFile = new File(""); //DUMMY - need to rewrite this method to play all files in media queue (see JavaMediaPlayer)
		Thread playerThread = new Thread() {

			@Override
			public void run() {
				doPlay(myMediaFile);
			}

		};
		playerThread.start();
	}

	private void doPlay(File mediaFile) {
		try {
			jPlayer = Manager.createPlayer(mediaFile.toURI().toURL());
			jPlayer.start();
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void stop() {
		jPlayer.stop();

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
		return null;
	}

	public void skip() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doCommand(PlayerCommand command) {
		// TODO Auto-generated method stub
		
	}	

}
