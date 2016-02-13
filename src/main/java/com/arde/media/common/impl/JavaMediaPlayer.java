package com.arde.media.common.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.arde.media.common.IMediaPlayer;
import com.arde.media.common.IPlayList;
import com.arde.media.common.Song;
import com.arde.media.common.SupportedMediaFilesFilter;
import com.arde.media.rmp.MediaPlayerQualifier;

@ApplicationScoped
@MediaPlayerQualifier("Java")
public class JavaMediaPlayer implements IMediaPlayer {
	private static Logger logger = Logger.getLogger(JavaMediaPlayer.class.getName());
	private PlayerState playerState = PlayerState.IDLE;
	
	/**
	 * The resource annotation is enough for this. The only reason the Inject annotation is added is because
	 * of unit tests - CDIRunner does not work for injecting resources, but adding hte Inject makes it work
	 */
//	@Resource
	//private ManagedThreadFactory managedThreadFactory; //use this to start threads so that CDI context is available to threads

	private JavaSoundPlayer jsp;

	private IPlayList playList;
//	private CdiAwarePlayCompletedListener playCompletedListener;
	
/*	@PostConstruct
	public void startListenerThread() {
		playCompletedListener = new CdiAwarePlayCompletedListener(this, managedThreadFactory);
		playCompletedListener.start();
	}*/
	
	public void doCommand(PlayerCommand command) {
		logger.info("doCommand " + command + ": current player state=" + getPlayerState());
		//do appropriate action
		switch(command) {
		case PAUSE_OR_RESUME:
			if (getPlayerState() == PlayerState.PLAYING) {
				pausePlay();
			} else if (getPlayerState() == PlayerState.PAUSED){
				resumePlay();
			}
			break;
		case PAUSE:
			if (getPlayerState() == PlayerState.PLAYING) {
				pausePlay();
			}
			break;
		case PLAY:
			if (getPlayerState() == PlayerState.PAUSED) {
				resumePlay();
			} else if (getPlayerState() != PlayerState.PLAYING) {
				playNextFile();
			}			
			break;
		case RESUME:
			if (getPlayerState() == PlayerState.PAUSED) {
				resumePlay();
			}
			break;
		case SKIP:
			jsp.stop();
			setPlayerState(PlayerState.STOPPED);
			playNextFile();
			break;
		case STOP:
			jsp.stop();
			setPlayerState(PlayerState.STOPPED);
			playList.songChanged(null);
			break;
		default:
			break;

		}
	}

	private void playNextFile() {
		Song myMedia = playList.getNextSongToPlay();
		File myMediaFile = (myMedia != null) ? myMedia.getFile() : null;
		if (myMediaFile != null && myMediaFile.canRead()) {
			setPlayerState(PlayerState.PLAYING);

			jsp = new JavaSoundPlayer(p -> this.doCommand(PlayerCommand.SKIP));
			jsp.play(myMediaFile);
			playList.songChanged(myMedia);
		} else {
			logger.warning("Either nothing left in queue to play, or could not open media file. Media file = " + myMediaFile);
			//nothing to play, idle the player
			setPlayerState(PlayerState.IDLE);
		}
	}

	private void resumePlay() {
		jsp.resume();
		setPlayerState(PlayerState.PLAYING);
	}

	private void pausePlay() {
		jsp.pause();
		setPlayerState(PlayerState.PAUSED);
	}

	@Override
	@Produces @SupportedMediaFilesFilter
	public FileFilter getSupportedFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				String fName = f.getName().toUpperCase(); 
				return (!f.isDirectory() && 
						f.canRead() && 
						(fName.endsWith(".MP3") || fName.endsWith(".WAV") || fName.endsWith(".FLAC")));
			}
			
		};
	}

	@Override
	public PlayerState getPlayerState() {
		return playerState;
	}	

	private void setPlayerState(PlayerState state) {
		playerState = state;
	}
	
	/**
	 * A listener for natural completion of play, which will move to the next song
	 * This will be started as a managed thread, and wait to be informed of play completion.
	 * The reason we need a managed thread here is as follows: The play completion notification comes from the update() method
	 * which is called by the Java sound event dispatcher thread when a line stops playing. That thread does not have the CDI context.
	 * We need the CDI context for playing to work correctly, so we have this listener thread started using a managed thread factory waiting for such events
	 */
	/***private static class CdiAwarePlayCompletedListener {
		private ManagedThreadFactory factory;
		private JavaMediaPlayer mediaPlayer;
		public CdiAwarePlayCompletedListener(JavaMediaPlayer mediaPlayer, ManagedThreadFactory factory) {
			this.factory = factory;
			this.mediaPlayer = mediaPlayer;
		}
		
		public void wakeUp() {
			synchronized(this) {
				notifyAll();
			}
		}

		public void start() {
			factory.newThread(new Runnable() {
				
				@Override
				public void run() {
					logger.info("CdiAwarePlayCompletedListener thread started");
					while (true) {
						//go into wait until we receive a command via notify
						//at which point can wake up and respond to it
						synchronized(CdiAwarePlayCompletedListener.this) {
							try {
								logger.info("CdiAwarePlayCompletedListener thread going into wait()");
								CdiAwarePlayCompletedListener.this.wait();
							} catch (InterruptedException e) {
								logger.info("CdiAwarePlayCompletedListener thread wait encountered InterruptedException - " + e);
								break;
							}
						}
						logger.info("CdiAwarePlayCompletedListener thread awakened, performing command");
						mediaPlayer.doCommand(PlayerCommand.SKIP);
						logger.info("CdiAwarePlayCompletedListener thread completed performing command");
					}
				}
			}).start();
		}
	}***/

	@Override
	public void setPlayList(IPlayList playList) {
		this.playList = playList;
	}
}
