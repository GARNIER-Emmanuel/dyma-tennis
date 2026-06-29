package com.dyma.tennis.features.tournaments;

public class TournamentAlreadyExistsException extends RuntimeException {
    public TournamentAlreadyExistsException(String name) {
        super("Tournament with name " + name + " already exists.");
    }
}
