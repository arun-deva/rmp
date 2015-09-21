package com.arde.media.common.impl;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Plays and is aware of only one file - will be used by JavaMediaPlayer
 * @author dev
 *
 */
public class JavaSoundPlayer implements LineListener {
	private static Logger logger = Logger.getLogger(JavaSoundPlayer.class.getName());

	/**
	 * The line that is currently playing
	 */
	SourceDataLine line;

	private boolean lineStoppedManually;

	private PlayCompletedListener playCompletedListener;

	private File file;
	
	
	public JavaSoundPlayer(PlayCompletedListener p) {
		this.playCompletedListener = p;
	}
	public long getMicrosecondPosition() {
		return line.getMicrosecondPosition();
	}
	/*METHODS THAT DO ACTUAL WORK OF MEDIA PLAYING*/
	public void play(File file) {
		//always play in a separate thread
		new Thread() {
			@Override
			public void run() {
				doPlay(file);
			}
			
		}.start();
	}
	
	private void doPlay(File file) {
		AudioInputStream actualAudioIn = null;
		this.file = file;
		try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(file)) {
			AudioFormat baseFormat = audioIn.getFormat();
			System.out.println(baseFormat.getEncoding());
			AudioFormat decodedFormat = getDecodedFormat(baseFormat);

			//make new audio in stream based on decoded format
			actualAudioIn = AudioSystem.getAudioInputStream(decodedFormat, audioIn);

			//get data from audio system
			line =  getLine(decodedFormat);

			line.addLineListener(this);

			doPlay(decodedFormat, actualAudioIn);
			audioIn.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception playing file '" + file.getName() + "'", e);
		} finally {
			if (actualAudioIn != null) {
				try {
					actualAudioIn.close();
				} catch (IOException e) {}
			}
			if (line != null) line.close();
		}
	}
	
	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
		logger.info("Getting line");
		SourceDataLine res = null;
		DataLine.Info info =
				new DataLine.Info(SourceDataLine.class, audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		logger.info("Got line " + res);
		res.open(audioFormat);
		logger.info("opened line");
		return res;
	}
	
	private void doPlay(AudioFormat decodedFormat, AudioInputStream audioIn) throws Exception {
		byte[] data = new byte[4096];
		SourceDataLine localLine = line;
		if (localLine != null)
		{
			// Start
			line.start();
			int nBytesRead = 0;
			while (nBytesRead != -1)
			{
				//if this line was stopped, return to caller who will do cleanup
				
				//no space to write - busy wait until available - no need to wait if play was stopped i.e. line closed
				while (localLine.available() == 0 && localLine.isOpen());
				if (!localLine.isOpen()) break;
				//how much space is available to write on the line
				//don't read more than that
				int available = localLine.available();

				int maxBytesToRead = (available < data.length)?available:data.length;
				
				nBytesRead = audioIn.read(data, 0, maxBytesToRead);
				if (nBytesRead != -1) {
					localLine.write(data, 0, nBytesRead);
				}
			}
			// Stop
//			stop();
		}
	}

	public void resume() {
		if (!line.isRunning()) line.start();
	}

	public void pause() {
		stopLine();
	}

	public void stop() {
		stopLine();
		line.close();
	}
	
	private void stopLine() {
		if (line.isRunning()) {
			lineStoppedManually = true;
			line.stop();
		}
	}
	/**
	 * Numbers used here are verbatim from Javazoom
	 * @param baseFormat
	 * @return
	 */
	private AudioFormat getDecodedFormat(AudioFormat baseFormat) {
		//Do we need to "decode" the base format?
		if (AudioFormat.Encoding.PCM_SIGNED.equals(baseFormat.getEncoding()) ||
				AudioFormat.Encoding.PCM_UNSIGNED.equals(baseFormat.getEncoding())) {
			return baseFormat;
		}
		return
		    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
		    		baseFormat.getSampleRate(),
		                    16,
		                    baseFormat.getChannels(),
		                    baseFormat.getChannels() * 2,
		                    baseFormat.getSampleRate(),
		                    false);
	}
	
	@Override
	public void update(LineEvent lineEvent) {
		logger.info("Received line event " + lineEvent + " on thread: " + Thread.currentThread());
		if (lineEvent.getType() == LineEvent.Type.STOP) {
			logger.info("line stopped manually = " + lineStoppedManually + " on thread: " + Thread.currentThread());
			//if play stopped naturally, notify completed listener
			if (!lineStoppedManually && playCompletedListener != null) {
				playCompletedListener.accept(file);
			}
			lineStoppedManually = false;
		}
	}

	public interface PlayCompletedListener extends Consumer<File>{

	}
}
