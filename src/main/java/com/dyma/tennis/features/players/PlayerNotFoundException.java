package com.dyma.tennis.features.players;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(UUID identifier) {
        super("Player with this identifier : " + identifier + " does not exist");
    }
}
