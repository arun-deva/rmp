package com.arde.media.web;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.jboss.weld.environment.se.Weld;

/**
 * Main class that uses embedded Jetty
 * @author dev
 *
 */
public class RMP {

	public static void main(String[] args) throws Exception {
		Weld weld = new Weld();
		weld.initialize();
		System.out.println("In RMP.java");
		URI baseUri = UriBuilder.fromUri("http://localhost/").port(8888).build();
		Server jetty = JettyHttpContainerFactory.createServer(baseUri, new JerseyApp(), false); //create but don't start server
		
		String webAppPath = "/rmp";
		String restAppPath = "/rmp/rest"; //need this because Jersey does not honor ApplicationPath annotation on JerseyApp class above
		
		final Handler jerseyHandler = jetty.getHandler();
        
        final ContextHandler restCtxHandler = new ContextHandler();
        restCtxHandler.setContextPath(restAppPath);
        restCtxHandler.setHandler(jerseyHandler);

        final ResourceHandler webappResourceHandler = new ResourceHandler();
		webappResourceHandler.setBaseResource(Resource.newClassPathResource("/webapp"));

        final ContextHandler webappCtxHandler = new ContextHandler();
        webappCtxHandler.setContextPath(webAppPath);
        webappCtxHandler.setHandler(webappResourceHandler);
		
        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.addHandler(restCtxHandler);
        contexts.addHandler(webappCtxHandler);
        
        /*final ContextHandler rmpCtxHandler = new ContextHandler();
        rmpCtxHandler.setContextPath(contextPath);
        
        final ResourceHandler webappResourceHandler = new ResourceHandler();
		webappResourceHandler.setHandler(jetty.getHandler());
		webappResourceHandler.setBaseResource(Resource.newClassPathResource("/webapp"));

        final ContextHandler restCtxHandler = new ContextHandler();
        restCtxHandler.setContextPath(restAppPath);
        
        rmpCtxHandler.setHandler(webappResourceHandler);
        webappResourceHandler.setHandler(restCtxHandler);
        restCtxHandler.setHandler(jerseyHandler);
        */
        //server's root handler will be for /rmp
        jetty.setHandler(contexts);

		jetty.start();
	}

}
