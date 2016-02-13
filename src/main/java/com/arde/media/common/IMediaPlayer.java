package com.arde.media.common;

import java.io.FileFilter;

public interface IMediaPlayer {
	public enum PlayerCommand {
		NONE, PLAY, STOP, PAUSE, RESUME, SKIP, PAUSE_OR_RESUME
	}
	public static enum PlayerEventType {
		PLAY_STARTED, PLAY_STOPPED
	}
	public static enum PlayerState {
		IDLE, PLAYING, STOPPED, PAUSED
	}
	public void doCommand(PlayerCommand command);
	public FileFilter getSupportedFileFilter();
	public PlayerState getPlayerState();
	void setPlayList(IPlayList playList);
}
