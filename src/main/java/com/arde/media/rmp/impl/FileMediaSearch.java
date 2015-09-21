package com.arde.media.rmp.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.arde.common.fileutils.filetraverse.FileTraverseEvent;
import com.arde.common.fileutils.filetraverse.FileTraverseListener;
import com.arde.common.fileutils.filetraverse.FileTraverser;
import com.arde.media.common.IAudioFileTagEditor;
import com.arde.media.common.IMediaPlayer;
import com.arde.media.common.Song;
import com.arde.media.musicsource.MusicSource;
import com.arde.media.musicsource.MusicSourceUpdatedEvent;
import com.arde.media.rmp.IMediaSearch;
import com.arde.media.rmp.MediaPlayerQualifier;

@ApplicationScoped
public class FileMediaSearch implements IMediaSearch {
	private long numFiles;
	
	@Inject
	private IAudioFileTagEditor songInfoEditor;

	@Inject @MediaPlayerQualifier("Java")
	private IMediaPlayer mediaPlayer;
	
	
	private FileFilter comboFilter = null;
	
	@PostConstruct
	public void init() {
		//do this in post construct since injections will have been completed by then
		comboFilter = new FileFilter() {
			private FileFilter supportedMediaFilesFilter = mediaPlayer.getSupportedFileFilter();
			@Override
			public boolean accept(File f) {
				return (!f.isDirectory() && 
						f.canRead() && 
						((supportedMediaFilesFilter == null)?true:supportedMediaFilesFilter.accept(f)));
			}
		};
	}
	private String rootLocation;
	@Override
	public Collection<Song> findSongs(String regex) {
		System.out.println("Root Location:" + rootLocation);
		File rootDir = new File(rootLocation);
		final String finalRegEx = regex.toUpperCase();
		FileFilter srchFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return (f.getName().toUpperCase().indexOf(finalRegEx)>=0 &&
						comboFilter.accept(f));
			}
			
		};
		Collection<File> matchedFiles = getMatches(rootDir, srchFilter);
		Collection<Song> songs = new ArrayList<Song>();
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
	
	public File getSongFileByKey(String key) {
		return new File(getRootLocation(), key);
	}
	
	@Override
	public File findSongFileByKey(String key) {
		return new File(getRootLocation(), key);
	}
	
	@Override
	public Song findSongByKey(String key) {
		File songFile = findSongFileByKey(key);
		if (songFile.canRead()) {
			//Assume key is song location relative to root
			try {
				return makeSong(songFile);
			} catch (Exception e) {
				//ignore exceptions - null will be returned
				e.printStackTrace();
			}
		}
		return null;
	}
	private long countMatches(File rootDir, FileFilter srchFilter) {
		class CountListener implements FileTraverseListener {
			public long ct;
			@Override
			public void foundFile(FileTraverseEvent evt) {
				ct = evt.getFileIndex() + 1;
				
			}
			@Override
			public boolean continueTraversing() {
				return true;
			}
		};
		CountListener counter = new CountListener();
		FileTraverser t = new FileTraverser();
		t.traverse(counter, rootDir, srchFilter);
		return counter.ct;
	}
	
	private Collection<File> getMatches(File rootDir, FileFilter srchFilter) {
		final Collection<File> matches = new ArrayList<File>();
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
	private Song makeSong(File f) throws Exception {
		String key = makeKey(f);
		Song s = new Song(key, f);
		s.setSongInfo(songInfoEditor.readSongInfo(f));
//		s.setFile(f);
//		s.setTitle(f.getName());
		return s;
	}
	
	private String makeKey(File f) throws IOException {
		//get the "relative path" of f w.r.t. rootLocation
		//first convert both f and rootLocation to their canonical forms 
		//(i.e. absolute paths, with any symbolic links (and on Windows, case difference) resolved
		String childPath = f.getCanonicalPath();
		String parentPath = new File(getRootLocation()).getCanonicalPath();
		
		//childPath MUST start with parentPath
		if (!childPath.startsWith(parentPath)) {
			return null;
		}
		
		return childPath.substring(parentPath.length());
		
	}

	public void handleMusicSourceChanged(@Observes MusicSourceUpdatedEvent musicSourceEvt) {
		this.rootLocation = musicSourceEvt.getMusicSource().getLocation();
		numFiles = countMatches(new File(rootLocation), comboFilter);
	}

	public String getRootLocation() {
		return rootLocation;
	}

	@Override
	public Collection<Song> findRandomSongs(int howMany) {
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
		t.traverse(randomFileCollector, new File(rootLocation), comboFilter);
		return songs;
	}
	
	private File findRandomFile(File rootDir) {		
		//generate random number (r) between 1 and number of files/sub dirs
		//if rth item is a file - return it else find random song from this root
		File[] children = rootDir.listFiles();
		if (children.length == 0) return null;
		int r = (int) Math.floor(children.length*Math.random());
		if (children[r].isDirectory()) {
			return findRandomFile(children[r]);
		}
		return children[r];
	}

	public void setSongInfoEditor(IAudioFileTagEditor songInfoEditor) {
		this.songInfoEditor = songInfoEditor;
	}

	/**
	 * This setter is just for unit tests since we don't have CDI container for testing yet
	 * The live app uses CDI event (@see handleMusicSourceChanged)
	 * @param musicSource
	 */
	public void setRootLocation(String rootLoc) {
		this.rootLocation = rootLoc;
		numFiles = countMatches(new File(rootLocation), comboFilter);
	}

}
