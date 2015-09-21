package com.arde.common.fileutils.filetraverse;

public interface FileTraverseListener {
	public void foundFile(FileTraverseEvent evt);
	public boolean continueTraversing();
}
