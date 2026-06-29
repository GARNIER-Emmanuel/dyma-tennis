package com.dyma.tennis.features.players;

public class PlayerDataRetrievalException extends RuntimeException {

    public PlayerDataRetrievalException(Exception e) {
        super("Could not retrieve player data", e);
    }

}
