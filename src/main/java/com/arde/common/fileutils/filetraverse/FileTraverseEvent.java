package com.arde.common.fileutils.filetraverse;

import java.io.File;

public class FileTraverseEvent {
	File foundFile;
	long fileIndex;
	public FileTraverseEvent(File foundFile, long fileIndex) {
		super();
		this.foundFile = foundFile;
		this.fileIndex = fileIndex;
	}
	public File getFoundFile() {
		return foundFile;
	}
	public long getFileIndex() {
		return fileIndex;
	}
	
}
