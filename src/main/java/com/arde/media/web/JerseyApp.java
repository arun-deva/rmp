package com.arde.media.web;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/rest")
public class JerseyApp extends ResourceConfig {
    public JerseyApp() {
        packages("com.arde.media.web");
        register(JacksonFeature.class);
    }
}
