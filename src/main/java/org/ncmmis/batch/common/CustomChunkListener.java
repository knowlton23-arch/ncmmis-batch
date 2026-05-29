package org.ncmmis.batch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;

@Component
public class CustomChunkListener<I, O> implements ChunkListener<I, O> {

	private static final Logger log = LoggerFactory.getLogger(CustomChunkListener.class);
	
    @Override
    public void beforeChunk(Chunk<I> chunk) {
        log.info("Before starting a new chunk...");
    }

    @Override
    public void afterChunk(Chunk<O> chunk) {
    	log.info("Chunk processed and committed successfully.");
    }

    @Override
    public void onChunkError(Exception exception, Chunk<O> chunk) {
    	log.info("Error occurred during chunk processing.");
    }
}
