package com.arde.media.web.exceptionmappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.arde.media.common.exceptions.MusicSourceNotSetException;

@Provider
public class MusicSourceNotSetExceptionMapper implements ExceptionMapper<MusicSourceNotSetException> {

	@Override
	public Response toResponse(MusicSourceNotSetException exception) {

		return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(exception).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
