package com.arde.media.web.exceptionmappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
