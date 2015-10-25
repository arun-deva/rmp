package com.arde.media.web;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import com.arde.media.musicsource.IMusicSourceManager;
import com.arde.media.musicsource.MusicSource;
import com.arde.media.musicsource.MusicSourceInfo;
import com.arde.media.rmp.IMediaIndexManager;

@Path("musicsource")
@Produces("application/json")
public class MusicSourceController {
	@Inject
	private IMusicSourceManager musicSourceMgr;
	
	@Inject
	private IMediaIndexManager indexMgr;
	
	//specifying request mapping at method level
	//that is relative path starting at parent class mapping
	//(ie) this request mapping is actually "musicsource/update"
	@POST @Path("update") 
	@Consumes("application/json")
	public MusicSource updateMusicSource(MusicSource musicSource) {
		try {
			musicSourceMgr.updateMusicSource(musicSource);
		} catch (Throwable t) {
			throw new WebApplicationException(t);
		}
		return musicSourceMgr.getMusicSource();
	}
	
	@POST @Path("reindex") 
	@Consumes("application/json")
	/**
	 * Refresh the Elastic Search index from music source directory
	 * @param musicSource
	 * @return
	 */
	public void reIndexMusicSource(MusicSource musicSource) {
		try {
			indexMgr.index(musicSource);
		} catch (Throwable t) {
			throw new WebApplicationException(t);
		}
	}
	
	@GET @Path("currentMusicSource")
	public MusicSource getCurrentMusicSource() throws IOException {
		return musicSourceMgr.getMusicSource();
	}
	
	@GET @Path("info")
	public MusicSourceInfo getInfo() throws IOException {
		return musicSourceMgr.getMusicSourceInfo();
	}
}
