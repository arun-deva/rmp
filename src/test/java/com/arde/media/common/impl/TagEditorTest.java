package com.arde.media.common.impl;

import java.io.File;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arde.media.common.IAudioFileTagEditor;

//@RunWith(CdiRunner.class)
//@AdditionalPackages({AudioFileTagEditor.class, IAudioFileTagEditor.class})
public class TagEditorTest {
//	
//	@Inject
//	private IAudioFileTagEditor tagEditor;
	
	@Test
	public void testGetFormat() throws Exception {
		String songPath = "E:\\Users\\arundeva\\Pictures\\Proshow\\Washington\\music\\10 - Aahaya Gangai.wav";
		AudioFile f = AudioFileIO.read(new File(songPath));
		System.out.println(f.getAudioHeader().getFormat());
	}
}
