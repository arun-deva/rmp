package com.arde.media.musicsource.search;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.arde.media.common.Song;

public interface IMediaSearch {
	public List<Song> findSongs(String searchStr) throws IOException;
	public Song findSongByKey(String key) throws IOException;
	public Collection<Song> findRandomSongs(int howMany);
}
