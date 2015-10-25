package com.arde.media.rmp.impl;

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
import com.arde.media.rmp.IMediaSearch;
import com.arde.media.rmp.MediaPlayerQualifier;
import com.arde.media.rmp.MediaSearchQualifier;
import com.arde.media.rmp.MediaSearchType;

@ApplicationScoped
@MediaSearchQualifier(MediaSearchType.FILE)
public class FileMediaSearch implements IMediaSearch {
	private long numFiles;
	
	@Inject
	private IAudioFileTagEditor songInfoEditor;
	
	@Inject
	@SupportedMediaFilesFilter
	private FileFilter supportedFilesFilter;
	
	private String rootLocation;
	private Future<FileMediaIndexedEvent> indexingFuture;
	
	/**
	 * For unit test use only
	 * @param supportedFilesFilter the supportedFilesFilter to set
	 */
	void setSupportedFilesFilter(FileFilter supportedFilesFilter) {
		this.supportedFilesFilter = supportedFilesFilter;
	}

	/**
	 * For unit test use only
	 * @param indexingFuture the indexingFuture to set
	 */
	void setIndexingFuture(Future<FileMediaIndexedEvent> indexingFuture) {
		this.indexingFuture = indexingFuture;
	}

	final private AtomicBoolean indexReady = new AtomicBoolean(false);
	
	@Override
	public List<Song> findSongs(String regex) {
		System.out.println("Root Location:" + rootLocation);
		waitForIndexReady();
		File rootDir = new File(rootLocation);
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
	
	/**
	 * This will be called when Future<FileMediaIndexedEvent> is fired, representing the fact that music source has changed,
	 * and new music source is being indexed.
	 * @param indexedEvent
	 */
	public void fileMediaIndexingStarted(@Observes Future<FileMediaIndexedEvent> indexingFuture) {
		indexReady.compareAndSet(true, false);
		this.indexingFuture = indexingFuture;
	}

	public String getRootLocation() {
		return rootLocation;
	}

	@Override
	public Collection<Song> findRandomSongs(int howMany) {
		waitForIndexReady();
		final Collection<Song> songs = new HashSet<Song>();
		long r;
		final SortedSet<Long> rands = new TreeSet<Long>();
		//create a list of howMany random numbers each between
		//0 and numFiles
		for(int i=0; i<howMany; i++) {
			r = (long) Math.floor(numFiles*Math.random());
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
		t.traverse(randomFileCollector, new File(rootLocation), supportedFilesFilter);
		return songs;
	}
	
	private File findRandomFile(File rootDir) {		
		//generate random number (r) between 1 and number of files/sub dirs
		//if rth item is a file - return it else find random song from this root
		waitForIndexReady();
		File[] children = rootDir.listFiles();
		if (children.length == 0) return null;
		int r = (int) Math.floor(children.length*Math.random());
		if (children[r].isDirectory()) {
			return findRandomFile(children[r]);
		}
		return children[r];
	}

	private void waitForIndexReady() {
		if (indexReady.get()) return;
		if (indexingFuture == null) {
			throw new IllegalStateException("Music location has to be set before searching!");
		}
		try {
			FileMediaIndexedEvent indexedEvent = indexingFuture.get();
			indexReady.set(true);
			this.numFiles = indexedEvent.getNumMediaFiles();
			this.rootLocation = indexedEvent.getRootLocation();
			
		} catch (InterruptedException | ExecutionException e1) {
			throw new IllegalStateException("Search not ready - music location indexing failed with exception!", e1);
		}
	}

	public void setSongInfoEditor(IAudioFileTagEditor songInfoEditor) {
		this.songInfoEditor = songInfoEditor;
	}

}
