package com.arde.media.musicsource.search.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.arde.common.fileutils.filetraverse.FileTraverseEvent;
import com.arde.common.fileutils.filetraverse.FileTraverseListener;
import com.arde.common.fileutils.filetraverse.FileTraverser;
import com.arde.media.common.IAudioFileTagEditor;
import com.arde.media.common.IMediaPlayer;
import com.arde.media.common.Song;
import com.arde.media.common.SupportedMediaFilesFilter;
import com.arde.media.musicsource.IMusicSourceManager;
import com.arde.media.musicsource.search.IMediaSearch;
import com.arde.media.musicsource.search.MediaSearchQualifier;
import com.arde.media.musicsource.search.MediaSearchType;
import com.arde.media.musicsource.search.MusicSourceIndexed;
import com.arde.media.rmp.MediaPlayerQualifier;

@ApplicationScoped
@MediaSearchQualifier(MediaSearchType.FILE)
public class FileMediaSearch implements IMediaSearch {
	@Inject
	private IAudioFileTagEditor songInfoEditor;
	
	@Inject
	@SupportedMediaFilesFilter
	private FileFilter supportedFilesFilter;
	
	@Inject
	IMusicSourceManager musicSrcMgr;
	
	/**
	 * For unit test use only
	 * @param supportedFilesFilter the supportedFilesFilter to set
	 */
	void setSupportedFilesFilter(FileFilter supportedFilesFilter) {
		this.supportedFilesFilter = supportedFilesFilter;
	}
	
	@Override
	public List<Song> findSongs(String regex) {
		musicSrcMgr.waitForMusicSourceReady();
		File rootDir = new File(musicSrcMgr.getMusicSource().getLocation());
		final String finalRegEx = regex.toUpperCase();
		FileFilter srchFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return (f.getName().toUpperCase().indexOf(finalRegEx)>=0 &&
						supportedFilesFilter.accept(f));
			}
			
		};
		List<File> matchedFiles = getMatches(rootDir, srchFilter);
		List<Song> songs = new ArrayList<Song>();
		for(File file : matchedFiles) {
			try {
				songs.add(makeSong(file));
			} catch (Exception e) {
				//ignore this song
				e.printStackTrace();
			}
			
		}
		return songs;
	}

	@Override
	public Song findSongByKey(String key) throws IOException {
		return makeSong(key);
	}
	

	
	private List<File> getMatches(File rootDir, FileFilter srchFilter) {
		final List<File> matches = new ArrayList<File>();
		FileTraverseListener counter = new FileTraverseListener() {
			@Override
			public void foundFile(FileTraverseEvent evt) {
				matches.add(evt.getFoundFile());
				
			}
			@Override
			public boolean continueTraversing() {
				return true;
			}
		};
		FileTraverser t = new FileTraverser();
		t.traverse(counter, rootDir, srchFilter);
		return matches;
	}
	private Song makeSong(File f) throws IOException {
		Song s = new Song(f);
		s.setSongInfo(songInfoEditor.readSongInfo(f));
		return s;
	}
	
	private Song makeSong(String key) throws IOException {
		Song s = new Song(key);
		s.setSongInfo(songInfoEditor.readSongInfo(s.getFile()));
		return s;
	}

	@Override
	public Collection<Song> findRandomSongs(int howMany) {
		musicSrcMgr.waitForMusicSourceReady();
		final Collection<Song> songs = new HashSet<Song>();
		long r;
		final SortedSet<Long> rands = new TreeSet<Long>();
		//create a list of howMany random numbers each between
		//0 and numFiles
		for(int i=0; i<howMany; i++) {
			r = (long) Math.floor(musicSrcMgr.getMusicSource().getNumSongs()*Math.random());
			rands.add(r);
		}
		
		FileTraverseListener randomFileCollector = new FileTraverseListener() {
			Iterator<Long> randIter = rands.iterator();
			long idx = randIter.next();
			private boolean continueTraversing = true;
			@Override
			public void foundFile(FileTraverseEvent evt) {
				if (!randIter.hasNext()) {
					continueTraversing = false;
					return;
				}
				if (evt.getFileIndex() == idx) {
					try {
						songs.add(makeSong(evt.getFoundFile()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					idx = randIter.next();
					
				}
			}
			@Override
			public boolean continueTraversing() {
				return continueTraversing ;
			}
		};
		FileTraverser t = new FileTraverser();
		t.traverse(randomFileCollector, new File(musicSrcMgr.getMusicSource().getLocation()), supportedFilesFilter);
		return songs;
	}

	public void setSongInfoEditor(IAudioFileTagEditor songInfoEditor) {
		this.songInfoEditor = songInfoEditor;
	}

}
