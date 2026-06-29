package com.dyma.tennis.features.tournaments;

public class TournamentDataRetrievalException extends RuntimeException {
    public TournamentDataRetrievalException(Exception e) {
        super("Could not retrieve tournament data", e);
    }
}
