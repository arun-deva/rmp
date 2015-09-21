package com.arde.media.rmp;

import java.util.List;

import com.arde.media.common.Song;

public interface IPlayList {

	public void addSong(Song s);

	public void removeSong(Song s);

	public void removeSong(String key);

	public List<Song> getList();

}
