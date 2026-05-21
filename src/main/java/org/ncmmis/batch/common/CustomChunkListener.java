package org.ncmmis.batch.common;

import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;

@Component
public class CustomChunkListener<I, O> implements ChunkListener<I, O> {

    @Override
    public void beforeChunk(Chunk<I> chunk) {
        System.out.println(">> Before starting a new chunk...");
    }

    @Override
    public void afterChunk(Chunk<O> chunk) {
        System.out.println(">> Chunk processed and committed successfully.");
    }

    @Override
    public void onChunkError(Exception exception, Chunk<O> chunk) {
        System.err.println("!! Error occurred during chunk processing.");
    }
}
