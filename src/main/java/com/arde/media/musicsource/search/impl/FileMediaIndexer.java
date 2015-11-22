package com.arde.media.musicsource.search.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.arde.common.fileutils.filetraverse.FileTraverseEvent;
import com.arde.common.fileutils.filetraverse.FileTraverseListener;
import com.arde.common.fileutils.filetraverse.FileTraverser;
import com.arde.media.common.MusicSource;
import com.arde.media.common.SupportedMediaFilesFilter;
import com.arde.media.musicsource.search.IMediaIndexer;
import com.arde.media.musicsource.search.MediaSearchQualifier;
import com.arde.media.musicsource.search.MediaSearchType;
import com.arde.media.musicsource.search.MusicSourceIndexed;

@ApplicationScoped
@MediaSearchQualifier(MediaSearchType.FILE)
public class FileMediaIndexer implements IMediaIndexer {

	@Inject
	@SupportedMediaFilesFilter
	private FileFilter supportedMediaFilesFilter;
	
	@Inject
	private Event<Future<MusicSourceIndexed>> evt;
	
	@Override
	public Future<MusicSourceIndexed> indexMusicSource(MusicSource musicSource) {
		//counts the number of files in the music source
		ExecutorService executorSvc = Executors.newSingleThreadExecutor();
		Callable<MusicSourceIndexed> indexingCallable = new Callable<MusicSourceIndexed>() {
			@Override
			public MusicSourceIndexed call() throws Exception {
				long numMediaFiles = countMatches(new File(musicSource.getLocation()), supportedMediaFilesFilter);
				return new MusicSourceIndexed(numMediaFiles);
			}
		};
		Future<MusicSourceIndexed> indexingFuture = executorSvc.submit(indexingCallable);
		return indexingFuture;
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

}
