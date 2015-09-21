package com.arde.media.common;

import java.io.File;

public interface IAudioFileTagEditor extends IAudioFileTagReader {
	public void setTitle(File mediaFile, String title) throws Exception;
	public void setAlbum(File mediaFile, String album) throws Exception;
	public void setArtist(File mediaFile, String artist) throws Exception;
	public void setRating(File mediaFile, int rating) throws Exception;
	public void writeSongInfo(File mediaFile, SongInfo updatedSongInfo) throws Exception;
}
