package com.arde.media.common.exceptions;

public class MusicSourceNotSetException extends RuntimeException {

	public MusicSourceNotSetException(String message) {
		super(message);
	}

	public MusicSourceNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

}
