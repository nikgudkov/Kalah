package org.task.kalah.dto;

public enum GameStatus {

    NOT_STARTED,
    STARTING,
    INITIALIZED,
    ENDED;

    public boolean isPlayable() {
        return this == INITIALIZED;
    }

}
