package com.arde.media.web;

import java.io.File;
import java.io.IOException;

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
import com.arde.media.musicsource.search.IMediaSearch;
import com.arde.media.musicsource.search.MediaSearchQualifier;
import com.arde.media.rmp.MetadataChangedEventQualifier;

@Path("tagEdit")
public class TagEditController {
	@Inject
	private IAudioFileTagEditor editorBean;
	
	//specifying request mapping at method level
	//that is relative path starting at parent class mapping
	//(ie) this request mapping is actually "edit/update"
	@POST @Path("update")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTags(Song song) throws Exception {
		System.out.println("Before Updating song: " + song.getSongInfo().getTitle());
		editorBean.writeSongInfo(song);
		System.out.println("Updated song: " + song);
	}
}
