package com.dyma.tennis.features.players;

import java.time.LocalDate;

public class PlayerAlreadyExistsException extends RuntimeException {

    public PlayerAlreadyExistsException(String firstName, String lastName, LocalDate birthDate) {
        super("Player with this firstName : " + firstName + " and lastName : " + lastName + " and birthDate : "
                + birthDate + " already exists");
    }
}
