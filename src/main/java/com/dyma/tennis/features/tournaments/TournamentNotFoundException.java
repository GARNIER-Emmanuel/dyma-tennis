package com.dyma.tennis.features.tournaments;

import java.util.UUID;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException(UUID identifier) {
        super("Tournament with identifier " + identifier + " could not be found.");
    }
}
