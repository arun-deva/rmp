package com.arde.media.musicsource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
	private Event<MusicSourceUpdatedEvent> musicSourceUpdatedEvent;
	private MusicSource musicSource;

	@PostConstruct
	private void initMusicSource() {
		try {
			InitialContext ctx = new InitialContext();
			String location = (String) ctx.lookup(MUSIC_SOURCE_JNDI_NAME);
			musicSource = new MusicSource(location);
			musicSourceUpdatedEvent.fire(new MusicSourceUpdatedEvent(musicSource));
			
		} catch (NamingException ne) {
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
	public void updateMusicSource(MusicSource musicSource) throws NamingException {
		InitialContext ctx = new InitialContext();
		Context rmpCtx = createSubContext(ctx, "rmp");
		rmpCtx.rebind("musicSource", musicSource.getLocation());
		this.musicSource = musicSource;
		musicSourceUpdatedEvent.fire(new MusicSourceUpdatedEvent(musicSource));
	}

	private Context createSubContext(InitialContext ctx, String subctx) throws NamingException {
		try {
			return (Context) ctx.lookup(subctx);
		} catch (NamingException ne) {
			//subctx not found, create it
			return ctx.createSubcontext(subctx);
		}
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
