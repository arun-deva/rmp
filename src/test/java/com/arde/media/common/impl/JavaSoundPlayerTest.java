package com.arde.media.common.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Ignore;
import org.junit.Test;

public class JavaSoundPlayerTest {
	static File myLongFile = new File("E:\\Users\\dev\\Music\\AprilMayile.mp3");
	static File myShortFile = new File("E:\\Users\\dev\\Music\\announcement.mp3");
/*	public static void main(String[] args) throws InterruptedException {
		runStartPauseStop();
//		runPlayCompletedNaturally();
	}*/
	
	@Test
	@Ignore
	public void testPlayCompletedNaturally() throws InterruptedException {
		final AtomicBoolean playCompletedCalled = new AtomicBoolean(false);
		CountDownLatch waitForPlayComplete = new CountDownLatch(1);
		final JavaSoundPlayer p = new JavaSoundPlayer(
				f -> {
					playCompletedCalled.set(true);
					System.out.println("runPlayCompletedNaturally: playCompleted listener called");
					waitForPlayComplete.countDown();
					});
		p.play(myShortFile);
		//use a countdown latch to wait until any threads spawned by the player are completed.
		waitForPlayComplete.await(6, TimeUnit.SECONDS);
		assertTrue("Expected playCompleted to be called for natural end of play!", playCompletedCalled.get());
	}
	
	@Test
	@Ignore
	public void testStartPauseStop() throws InterruptedException {
		// TODO Auto-generated method stub
		final AtomicBoolean playCompletedCalled = new AtomicBoolean(false);
		final JavaSoundPlayer p = new JavaSoundPlayer(
				f -> {
					playCompletedCalled.set(true);
					System.out.println("runStartPauseStop: playCompleted listener called unexpectedly!");
					});
		Thread myThread = new Thread(new Runnable() {

			@Override
			public void run() {
				p.play(myLongFile);
			}
			
		});
		long pausePos = 0;
		myThread.start();
		Thread.sleep(2000);
		p.pause();
		pausePos = (p.getMicrosecondPosition()/1000);
		System.out.println("Position after pause:" + pausePos);
		assertTrue(pausePos > 0 && pausePos <= 2000);
		Thread.sleep(4000);
		
		p.resume();
		Thread.sleep(3000);
		long preStopPos = (p.getMicrosecondPosition()/1000);
		System.out.println("Position before stop:" + preStopPos);
		//it must have run for around 3 seconds after resume (total of 5 seconds run time, not counting sleep during pause)
		assertTrue(preStopPos - pausePos > 0 && preStopPos <= 5000);
		
		p.stop();
		assertEquals("Player not stopped after call to stop()", 0, p.getMicrosecondPosition());

		System.out.println("Position after stop:" + p.getMicrosecondPosition());

		assertFalse("Unexpected call to playCompleted listener! It should not be called when play is interrupted manually", playCompletedCalled.get());
	}
}
