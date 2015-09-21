package com.arde.media.common;

import java.io.File;

public interface IAudioFileTagReader {
	public SongInfo readSongInfo(File f) throws Exception;
}
