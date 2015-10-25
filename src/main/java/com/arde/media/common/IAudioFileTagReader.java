package com.arde.media.common;

import java.io.File;
import java.io.IOException;

public interface IAudioFileTagReader {
	public SongInfo readSongInfo(File f) throws IOException;
}
