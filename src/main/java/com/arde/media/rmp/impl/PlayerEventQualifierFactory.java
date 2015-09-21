package com.arde.media.rmp.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.arde.media.common.IMediaPlayer.PlayerEventType;
import com.arde.media.common.Song;
import com.arde.media.rmp.PlayerEventQualifier;

@ApplicationScoped
public class PlayerEventQualifierFactory {
	
	@Inject
	@Any
	private Event<Song> playerEvent;
	
	//AnnotationLiteral class to enable us to use enum to dynamically select CDI event qualifier
	private static abstract class PlayerEventQualifierAnnotationLiteral 
	extends AnnotationLiteral<PlayerEventQualifier> implements PlayerEventQualifier {};
	
	private PlayerEventQualifier getPlayerEventQualifierAnnotation(final PlayerEventType event) {
		return new PlayerEventQualifierAnnotationLiteral() {
			@Override
			public PlayerEventType type() {
				return event;
			}
		};
	}
	
	public Event<Song> getQualifiedPlayerEvent(PlayerEventType event) {
		return playerEvent.select(getPlayerEventQualifierAnnotation(event));
	}
}
