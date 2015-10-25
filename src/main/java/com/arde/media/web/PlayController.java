package com.arde.media.web;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.arde.media.common.Song;
import com.arde.media.rmp.IMediaSearch;
import com.arde.media.rmp.IPlaylistManager;
import com.arde.media.rmp.MediaSearchQualifier;
import com.arde.media.rmp.MediaSearchType;
import com.arde.media.rmp.impl.PlayListStatus;

@Path("play")
@Produces("application/json")
//@ManagedBean
public class PlayController {
	@Inject
	private IPlaylistManager playListMgr;
	
	@Inject @MediaSearchQualifier(MediaSearchType.ELASTICSEARCH)
	private IMediaSearch searchBean;
	
	//specifying request mapping at method level
	//that is relative path starting at parent class mapping
	//(ie) this request mapping is actually "play/start"
	@POST @Path("add")
	public void addSong(@QueryParam("key") String key) throws IOException {
		Song s = searchBean.findSongByKey(key);
		if (s != null) {
			playListMgr.addSong(s);
		}
	}

	@POST @Path("random")
	public void addRandomSongs() {
		Collection<Song> s = searchBean.findRandomSongs(20);
		if (s != null) {
			for(Song song: s)  {
				playListMgr.addSong(song);
			}
		}
	}
	@POST @Path("stop")
	public void stop() {
		playListMgr.stopPlaying();
	}
	
	@POST @Path("skip")
	public void skip() {
		playListMgr.skip();
	}
	
	@POST @Path("pause")
	public void pauseOrResume() {
		playListMgr.pauseOrResume();
	}
	
	@POST @Path("remove")
	public void removeSong(@QueryParam("key") String key) {
		playListMgr.removeSong(key);
	}
	
	@GET @Path("playList")
	public List<Song> getPlayList() {
		return playListMgr.getPlayList();
	}
	
	@GET @Path("playListStatus")
	public PlayListStatus getStatus() {		
		return playListMgr.getPlayListStatus();
		
	}
	
}
