package com.arde.media.common.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import com.arde.media.common.IAudioFileTagEditor;
import com.arde.media.common.Song;
import com.arde.media.common.SongInfo;
import com.arde.media.rmp.MetadataChangedEventQualifier;

@ApplicationScoped
public class AudioFileTagEditor implements IAudioFileTagEditor {
	@Inject
	@MetadataChangedEventQualifier
	private Event<Song> songMetaDataChangedEvt;
	
	@Override
	public void setArtist(File mediaFile, String artist) throws Exception {
		AudioFile f = AudioFileIO.read(mediaFile);
		Tag tag = f.getTag();
		
		System.out.println("Current Artist: " + tag.getFirst(FieldKey.ARTIST));
		tag.setField(FieldKey.ARTIST, artist);
		f.commit();
	}

	@Override
	public void setRating(File mediaFile, int rating) throws Exception {
		VorbisCommentTag vTag = null;
		AudioFile f = AudioFileIO.read(mediaFile);
		Tag tag = f.getTag();
		if (tag instanceof FlacTag)  {
			vTag = ((FlacTag) tag).getVorbisCommentTag();
		}
		else if (tag instanceof VorbisCommentTag)  {
			vTag = (VorbisCommentTag) tag;
		}
		if (vTag != null)  {
			vTag.setField(vTag.createField("RMP_RATING",String.valueOf(rating)));				
			f.commit();
		}	

	}

	@Override
	public void setTitle(File mediaFile, String title) throws Exception {
		AudioFile f = AudioFileIO.read(mediaFile);
		Tag tag = f.getTag();
		System.out.println("Current Title: " + tag.getFirst(FieldKey.TITLE));
		tag.setField(FieldKey.TITLE, title);
		f.commit();
	}

	@Override
	public void setAlbum(File mediaFile, String album) throws Exception {
		AudioFile f = AudioFileIO.read(mediaFile);
		Tag tag = f.getTag();
		System.out.println("Current Album: " + tag.getFirst(FieldKey.ALBUM));
		tag.setField(FieldKey.ALBUM, album);
		f.commit();
	}

	@Override
	public SongInfo readSongInfo(File mediaFile) throws IOException {
		SongInfo info = new SongInfo();
		try {
			AudioFile f = AudioFileIO.read(mediaFile);
			Tag tag = f.getTag();
			info.setTitle(tag.getFirst(FieldKey.TITLE));
			info.setArtist(tag.getFirst(FieldKey.ARTIST));
			info.setAlbum(tag.getFirst(FieldKey.ALBUM));
			info.setRating(getRating(tag));
			info.setEditable(isTagEditable(f));
			return info;
		} catch (InvalidAudioFrameException | CannotReadException | TagException | ReadOnlyFileException e) {
			throw new IOException("Tag reader failed to read audio file", e);
		}
		
	}

	private boolean isTagEditable(AudioFile f) {
		//wav files are not editable in jaudiotagger 2.0.x
		return (!f.getAudioHeader().getFormat().startsWith("WAV"));
	}

	private int getRating(Tag tag) {
		VorbisCommentTag vTag = null;
		if (tag instanceof FlacTag)  {
			vTag = ((FlacTag) tag).getVorbisCommentTag();
		}
		else if (tag instanceof VorbisCommentTag)  {
			vTag = (VorbisCommentTag) tag;
		}
		if (vTag != null)  {
			List<TagField> fldList = vTag.get("RMP_RATING");
			if (fldList.size() > 0) {
				TagField fld = fldList.get(0);
				return Integer.parseInt(fld.toString());
			}
		}
		return 0;
	}

	@Override
	public void writeSongInfo(Song song) throws Exception {
		SongInfo updatedSongInfo = song.getSongInfo();
		AudioFile f = AudioFileIO.read(song.getFile());
		Tag tag = f.getTag();
		
		System.out.println("Current Artist: " + tag.getFirst(FieldKey.ARTIST));
		tag.setField(FieldKey.TITLE, updatedSongInfo.getTitle());
		tag.setField(FieldKey.ARTIST, updatedSongInfo.getArtist());
		tag.setField(FieldKey.ALBUM, updatedSongInfo.getAlbum());
		setRating(tag, updatedSongInfo.getRating());
		f.commit();
		songMetaDataChangedEvt.fire(song);
	}
	
	private void setRating(Tag tag, int rating) throws Exception {
		VorbisCommentTag vTag = null;
		if (tag instanceof FlacTag)  {
			vTag = ((FlacTag) tag).getVorbisCommentTag();
		}
		else if (tag instanceof VorbisCommentTag)  {
			vTag = (VorbisCommentTag) tag;
		}
		if (vTag != null)  {
			vTag.setField(vTag.createField("RMP_RATING",String.valueOf(rating)));				
		}	
	}
}
