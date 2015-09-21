package com.arde.media.web;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import com.arde.media.musicsource.IMusicSourceManager;
import com.arde.media.musicsource.MusicSource;
import com.arde.media.musicsource.MusicSourceInfo;

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
	public MusicSource updateMusicSource(MusicSource musicSource) {
		try {
			musicSourceMgr.updateMusicSource(musicSource);
		} catch (Throwable t) {
			throw new WebApplicationException(t);
		}
		return musicSourceMgr.getMusicSource();
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
