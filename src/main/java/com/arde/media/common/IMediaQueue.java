package com.arde.media.common;

import java.io.File;

public interface IMediaQueue {
	/**
	 * Expected to block until file available
	 * @return
	 */
	public Song getNextSongToPlay();
}
