package com.arde.media.web;

import java.io.File;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.arde.media.common.IAudioFileTagEditor;
import com.arde.media.common.Song;
import com.arde.media.common.SongInfo;
import com.arde.media.rmp.IMediaSearch;
import com.arde.media.rmp.MetadataChangedEventQualifier;

@Path("tagEdit")
public class TagEditController {
	@Inject
	private IMediaSearch searchBean;
	
	@Inject
	private IAudioFileTagEditor editorBean;
	
	@Inject
	@MetadataChangedEventQualifier
	private Event<Song> songMetaDataChangedEvt;
	
	//specifying request mapping at method level
	//that is relative path starting at parent class mapping
	//(ie) this request mapping is actually "edit/update"
	@POST @Path("update")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTags(Song song) throws Exception {
		//retrieve file for given song key since file path won't be in the incoming song in request
		File songFile = searchBean.findSongFileByKey(song.getKey());
		song.setFile(songFile);
		System.out.println("Before Updating song: " + song.getSongInfo().getTitle());
		editorBean.writeSongInfo(song.getFile(), song.getSongInfo());
		songMetaDataChangedEvt.fire(song);
		System.out.println("Updated song: " + song);
	}
	//specifying request mapping at method level
	//that is relative path starting at parent class mapping
	//(ie) this request mapping is actually "edit/update"
	@GET @Path("get")
	public SongInfo getTags(@QueryParam("key") String key) {
		Song s = searchBean.findSongByKey(key);
		try {
			return s.getSongInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
