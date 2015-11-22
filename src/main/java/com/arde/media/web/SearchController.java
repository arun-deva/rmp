package com.arde.media.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.arde.media.common.Song;
import com.arde.media.musicsource.search.IMediaSearch;
import com.arde.media.musicsource.search.MediaSearchQualifier;
import com.arde.media.musicsource.search.MediaSearchType;

@Path("search")
public class SearchController {
	@Inject @MediaSearchQualifier(MediaSearchType.ELASTICSEARCH)
	private IMediaSearch searchBean;
	
	@GET
	public Collection<Song> findSongs(@QueryParam("searchText") String srchPattern) throws IOException {
		if (srchPattern == null) {
			return new ArrayList<Song>();
		}
		
		return searchBean.findSongs(srchPattern);
	}
	
}
