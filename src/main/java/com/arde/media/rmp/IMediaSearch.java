package com.arde.media.rmp;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;

import com.arde.media.common.Song;

public interface IMediaSearch {
	public Collection<Song> findSongs(String searchStr);
	public Song findSongByKey(String key);
	public File getSongFileByKey(String key);
	public Collection<Song> findRandomSongs(int howMany);
	public File findSongFileByKey(String key);
}
