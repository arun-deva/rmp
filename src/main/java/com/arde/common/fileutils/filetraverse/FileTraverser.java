package com.arde.common.fileutils.filetraverse;

import java.io.File;
import java.io.FileFilter;


public class FileTraverser {
	private static FileFilter directoryFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
		
	};
	private static FileFilter filesFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return (!pathname.isDirectory()) && pathname.canRead();
		}
		
	};
	long index = 0;
	private void doTraverse(FileTraverseListener listener, File rootDir, FileFilter srchFilter) {
		if (!listener.continueTraversing()) return;
		if (srchFilter == null) srchFilter = filesFilter;
		File[] matchingFiles = rootDir.listFiles(srchFilter);
		if (matchingFiles == null) return;
		for( File foundFile : matchingFiles)  {
			listener.foundFile(new FileTraverseEvent(foundFile, index));
			index++;
		}
		File[] subDirs = rootDir.listFiles(directoryFilter);
		for(File dir : subDirs) {
			doTraverse(listener, dir, srchFilter);
		}
	}
	
	public void traverse(FileTraverseListener listener, File rootDir, FileFilter srchFilter) {
		index = 0;
		doTraverse(listener, rootDir, srchFilter);
	}

}
