package org.task.kalah.service;

import java.util.concurrent.atomic.AtomicLong;


public class GameIdGenerator {

    private final AtomicLong currentId;

    public GameIdGenerator(AtomicLong currentId) {
        this.currentId = currentId;
    }

    public Long getNextId() {
        return currentId.incrementAndGet();
    }


}
