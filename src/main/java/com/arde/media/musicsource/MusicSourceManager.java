package com.arde.media.musicsource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.arde.media.common.MusicSource;
import com.arde.media.common.MusicSourceInfo;
import com.arde.media.common.exceptions.MusicSourceNotSetException;
import com.arde.media.musicsource.search.IMediaIndexer;
import com.arde.media.musicsource.search.MediaSearchQualifier;
import com.arde.media.musicsource.search.MediaSearchType;
import com.arde.media.musicsource.search.MusicSourceIndexed;

@ApplicationScoped
/**
 * Manages music source(s)
 * Fires CDI event: MusicSourceUpdatedEvent
 * @author dev
 *
 */
public class MusicSourceManager implements IMusicSourceManager {
	public static final String MUSIC_SOURCE_JNDI_NAME = "rmp/musicSource";
	
	@Inject
	@MediaSearchQualifier(MediaSearchType.ELASTICSEARCH)
	IMediaIndexer indexer;
	
	private MusicSource musicSource;

	private Future<MusicSourceIndexed> indexingFuture;

	@PostConstruct
	private void initMusicSource() {
		Optional<MusicSource> optMs = indexer.getSelectedMusicSource();
		if (optMs.isPresent()) musicSource = optMs.get();
		else {
			System.out.println("Lookup failed for music source location!");
			//this is ok - not an error condition since very first time, music source will be empty
		}
	}

	/* (non-Javadoc)
	 * @see com.arde.media.IMusicSourceManager#getMusicSource()
	 */
	@Override
	public MusicSource getMusicSource() {
		return musicSource;
	}

	@Override
	public void waitForMusicSourceReady() {
		if (musicSource == null) {
			throw new MusicSourceNotSetException("Music location is not set!");
		}
		if (indexingFuture == null) return; //no indexing has taken place
		if (indexingFuture.isDone()) return; //we are not in the middle of an indexing
		try {
			MusicSourceIndexed indexedInfo = indexingFuture.get();
			musicSource.setReady(true);
			musicSource.setNumSongs(indexedInfo.getNumMediaFiles());
		} catch (InterruptedException e1) {
			//separate catch blocks for this and execution exception because of some JSON mapping issues (circularity due
			//to some internal structure of the ExecutionException)
			throw new MusicSourceNotSetException("Music location indexing interrupted! " + e1.getMessage());
		} catch (ExecutionException e) {
			throw new MusicSourceNotSetException("Music location indexing failed! " + e.getCause().getMessage());
		}
	}
	
	@Override
	public void updateMusicSource(MusicSource musicSource) {
		indexer.setSelectedMusicSource(musicSource);
		this.musicSource = musicSource;
		musicSource.setReady(false);
		reIndexMusicSource();
	}

	@Override
	public void reIndexMusicSource() {
		indexingFuture = indexer.indexMusicSource(this.musicSource);
	}

	@Override
	public List<MusicSource> getRoots() throws IOException {
		//on Linux, /proc/mounts will contain the disk partitions, one of which can be the music source
		File mountPointsLinux = new File("/proc/mounts");
		List<MusicSource> potentialMusicSources = new ArrayList<MusicSource>();
		if (mountPointsLinux.canRead()) {
			try(LineNumberReader rdr = new LineNumberReader(new FileReader(mountPointsLinux))) {
				String line;
				while ((line = rdr.readLine()) != null) {
					//each line in /proc/mounts is of the form "devicename  mountDir ...." - we want mountDir
					String mountedDirName = line.split("\\s+")[1];
					potentialMusicSources.add(new MusicSource(mountedDirName));
				}
			}
			
		} else {
			//possibly a windows system with multiple roots
			File[] roots = File.listRoots();
			Arrays.asList(roots).forEach(f->potentialMusicSources.add(new MusicSource(f.getPath())));
		}
		return potentialMusicSources;
	}

	@Override
	public MusicSourceInfo getMusicSourceInfo() throws IOException {
		return new MusicSourceInfo(getMusicSource(), getRoots());
	}

}
