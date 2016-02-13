package com.arde.media.web;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import com.arde.media.common.MusicSource;
import com.arde.media.common.MusicSourceInfo;
import com.arde.media.musicsource.IMusicSourceManager;

@Path("musicsource")
@Produces("application/json")
public class MusicSourceController {
	@Inject
	private IMusicSourceManager musicSourceMgr;
	
	//specifying request mapping at method level
	//that is relative path starting at parent class mapping
	//(ie) this request mapping is actually "musicsource/update"
	@POST @Path("update") 
	@Consumes("application/json")
	public void updateMusicSource(MusicSource musicSource) {
		try {
			musicSourceMgr.updateMusicSource(musicSource);
		} catch (Throwable t) {
			throw new WebApplicationException(t);
		}
	}
	
	@POST @Path("reindex") 
	@Consumes("application/json")
	/**
	 * Refresh the Elastic Search index from music source directory
	 * @param musicSource
	 * @return
	 */
	public void reIndexMusicSource() {
		try {
			musicSourceMgr.reIndexMusicSource();
		} catch (Throwable t) {
			throw new WebApplicationException(t);
		}
	}
/*	
	@GET @Path("currentMusicSource")
	public MusicSource getCurrentMusicSource() throws IOException {
		return musicSourceMgr.getMusicSource();
	}*/
	
	@GET @Path("info")
	public MusicSourceInfo getInfo() {
		try {
			return musicSourceMgr.getMusicSourceInfo();
		} catch (Throwable t) {
			t.printStackTrace();
			throw new WebApplicationException(t);
		}
	}
}
