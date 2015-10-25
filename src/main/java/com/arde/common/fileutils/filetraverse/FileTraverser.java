package com.arde.common.fileutils.filetraverse;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;


public class FileTraverser {
	private Logger logger = Logger.getLogger(this.getClass().getName());
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
		logger.info("doTraverse directory '" + rootDir.getAbsolutePath());
		File[] matchingFiles = rootDir.listFiles(srchFilter);
		if (matchingFiles == null) {
			logger.info("doTraverse matchingFiles = null");
			return;
		}
		for( File foundFile : matchingFiles)  {
			logger.info("doTraverse foundFile:" + foundFile.getAbsolutePath());
			listener.foundFile(new FileTraverseEvent(foundFile, index));
			index++;
		}
		File[] subDirs = rootDir.listFiles(directoryFilter);
		for(File dir : subDirs) {
			logger.info("doTraverse sub directory '" + dir.getAbsolutePath());
			doTraverse(listener, dir, srchFilter);
		}
	}
	
	public void traverse(FileTraverseListener listener, File rootDir, FileFilter srchFilter) {
		index = 0;
		doTraverse(listener, rootDir, srchFilter);
	}

}
