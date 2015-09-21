package com.arde.media.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Initializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
//		Logger.getLogger("org.jaudiotagger.audio").setLevel(Level.OFF);
//		Logger.getLogger("org.jaudiotagger.tag.id3").setLevel(Level.OFF);
//		Logger.getLogger("org.jaudiotagger.tag.datatype").setLevel(Level.OFF);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
