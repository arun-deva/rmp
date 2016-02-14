package com.arde.media.musicsource.search.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.arde.common.fileutils.filetraverse.FileTraverseEvent;
import com.arde.common.fileutils.filetraverse.FileTraverseListener;
import com.arde.common.fileutils.filetraverse.FileTraverser;
import com.arde.media.common.IAudioFileTagEditor;
import com.arde.media.common.MusicSource;
import com.arde.media.common.Song;
import com.arde.media.common.SupportedMediaFilesFilter;
import com.arde.media.musicsource.search.ElasticSearchConstants;
import com.arde.media.musicsource.search.IElasticSearchClient;
import com.arde.media.musicsource.search.IMediaIndexer;
import com.arde.media.musicsource.search.Indexable;
import com.arde.media.musicsource.search.MediaSearchQualifier;
import com.arde.media.musicsource.search.MediaSearchType;
import com.arde.media.musicsource.search.MusicSourceIndexed;
import com.arde.media.rmp.MetadataChangedEventQualifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
@MediaSearchQualifier(MediaSearchType.ELASTICSEARCH)
public class ElasticSearchIndexer implements IMediaIndexer {
	@Inject
	@SupportedMediaFilesFilter
	private FileFilter supportedMediaFilesFilter;

	@Inject
	IElasticSearchClient esClient;
	
	@Inject
	private IAudioFileTagEditor songInfoEditor;
	
	@Inject
	private Event<Future<MusicSourceIndexed>> evt;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/* (non-Javadoc)
	 * @see com.arde.media.rmp.impl.IElasticSearchIndexer#refreshIndex(java.lang.String)
	 */
	@Override
	public Future<MusicSourceIndexed> indexMusicSource(MusicSource musicSource) {
		ExecutorService executorSvc = Executors.newSingleThreadExecutor();
		Callable<MusicSourceIndexed> indexingCallable = new Callable<MusicSourceIndexed>() {
			@Override
			public MusicSourceIndexed call() throws Exception {
				long numSongs = buildElasticSearchIndex(musicSource);
				return new MusicSourceIndexed(numSongs);
			}
		};
		Future<MusicSourceIndexed> indexingFuture = executorSvc.submit(indexingCallable);
		return indexingFuture;
	}
	
	private long buildElasticSearchIndex(MusicSource musicSource) {
		esClient.removeIndex(ElasticSearchConstants.RMP_INDEX_NAME);
		FileTraverseListener indexerListener = new FileTraverseListener() {

			@Override
			public void foundFile(FileTraverseEvent evt) {
				try {
				Song song = new Song(evt.getFoundFile());
				song.setSongInfo(songInfoEditor.readSongInfo(evt.getFoundFile()));
				ObjectMapper mapper = new ObjectMapper();
				byte[] jsonDocument = mapper.writeValueAsBytes(song);
				
				esClient.bulkIndexDocument(
						ElasticSearchConstants.RMP_INDEX_NAME,
						ElasticSearchConstants.SONG_TYPE_NAME,
						song.getKey(), jsonDocument);

				logger.log(Level.INFO, String.format("Indexed file %s", evt.getFoundFile()));
				} catch (IOException e) {
					logger.log(
							Level.WARNING,
							String.format("Unable to index file '%s'", evt.getFoundFile()),
							e);
				}
			}
			
			@Override
			public boolean continueTraversing() {
				return true;
			}
		};
		
		esClient.startBulkIndexing();
		
		try {
			FileTraverser traverser = new FileTraverser();
			traverser.traverse(indexerListener, new File(musicSource.getLocation()), supportedMediaFilesFilter);
			logger.info("Finished traversing " + musicSource.getLocation());
		} finally {
			esClient.stopBulkIndexing();
			return esClient.getCount(ElasticSearchConstants.RMP_INDEX_NAME, ElasticSearchConstants.SONG_TYPE_NAME);
		}
	}
	
	public void songInfoUpdated(@Observes @MetadataChangedEventQualifier Song s) throws JsonProcessingException {
		esClient.index(ElasticSearchConstants.RMP_INDEX_NAME, ElasticSearchConstants.SONG_TYPE_NAME, new Indexable<Song>() {

			@Override
			public String getKey() {
				return s.getKey();
			}

			@Override
			public Song getEntity() {
				return s;
			}
		});
	}

	@Override
	public void setSelectedMusicSource(final MusicSource musicSource) {
		try {
			esClient.index(ElasticSearchConstants.RMP_INDEX_NAME, ElasticSearchConstants.MUSIC_SOURCE_TYPE_NAME, 
					new Indexable<MusicSource>() {

						@Override
						public String getKey() {
							return musicSource.getLocation();
						}

						@Override
						public MusicSource getEntity() {
							return musicSource;
						}
					});
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Set music source failed for music source: " + musicSource, e);
		}
	}

	@Override
	public Optional<MusicSource> getSelectedMusicSource() {
		Optional<List<MusicSource>> sources = esClient.getAll(
				ElasticSearchConstants.RMP_INDEX_NAME,
				ElasticSearchConstants.MUSIC_SOURCE_TYPE_NAME, 
				MusicSource.class);
		if (!sources.isPresent()) return Optional.empty();
		assert sources.get().size() <= 1;
		return Optional.of(sources.get().get(0));
	}

}
