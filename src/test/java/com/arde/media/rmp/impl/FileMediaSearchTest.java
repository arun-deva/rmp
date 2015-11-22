package com.arde.media.rmp.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arde.media.common.IMediaPlayer;
import com.arde.media.common.Song;
import com.arde.media.common.impl.JavaMediaPlayer;
import com.arde.media.common.impl.TestManagedThreadFactory;
import com.arde.media.musicsource.MusicSourceManager;
import com.arde.media.musicsource.search.IMediaSearch;
import com.arde.media.musicsource.search.MusicSourceIndexed;
import com.arde.media.musicsource.search.impl.FileMediaSearch;

@RunWith(CdiRunner.class)
@AdditionalPackages({FileMediaSearch.class, IMediaSearch.class, JavaMediaPlayer.class, TestManagedThreadFactory.class})

public class FileMediaSearchTest {
	private static final String ROOT_LOCATION = "E:\\Users\\dev\\Music";
	
	@Inject
	IMediaSearch srch;
	
	@Test
	public void testMediaSearch() throws Exception {
		IMediaSearch srch = getFileMediaSearch();
		System.out.println(srch.getClass().getName());
//		Assert.assertTrue(srch instanceof com.arde.media.impl.FileMediaSearch);
		Collection<Song> hits = srch.findSongs("kaattu");
		//assertTrue("Ahaya".matches("Aha"));
		
		for(Song s : hits) {
			System.out.println(s.getSongInfo().getTitle());
		}

		File songFile = hits.iterator().next().getFile();
		Assert.assertNotNull(songFile);
		Assert.assertEquals("KaattuKuyilu.mp3", songFile.getName());
	}
	@Test
	public void testRandomMediaSearch() throws Exception {
		IMediaSearch srch = getFileMediaSearch();
		Collection<Song> hits = srch.findRandomSongs(10);
		Assert.assertEquals(10, hits.size());
		//assertTrue("Ahaya".matches("Aha"));
		
		for(Song s : hits) {
			System.out.println(s.getKey());
		}
	}

	private IMediaSearch getFileMediaSearch() throws Exception {
		
//		Field mediaPlayerFld = srch.getClass().getDeclaredField("mediaPlayer");
//		mediaPlayerFld.setAccessible(true);
//		mediaPlayerFld.set(srch, getJavaMediaPlayer());
//		srch.setFileFilter(getJavaMediaPlayer().getSupportedFileFilter());
//		srch.setSongInfoEditor(new AudioFileTagEditor());
		return srch;
	}
	private Future<MusicSourceIndexed> getMockIndexingFuture() {
		return new Future<MusicSourceIndexed>() {

			private MusicSourceIndexed myevent = new MusicSourceIndexed(15);

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public MusicSourceIndexed get() throws InterruptedException,
					ExecutionException {
				return myevent;
			}

			@Override
			public MusicSourceIndexed get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				return myevent;
			}
		};
	}
	@Test
	public void testPotentialMusicSources() throws IOException {
		MusicSourceManager msm = new MusicSourceManager();
		System.out.println(msm.getRoots());
		//File[] roots = File.listRoots();
		//Arrays.asList(roots).forEach(f->potentialMusicSources.add(f.getName()));
	}
}
