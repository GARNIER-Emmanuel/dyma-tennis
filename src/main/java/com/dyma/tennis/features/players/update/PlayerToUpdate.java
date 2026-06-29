package com.dyma.tennis.features.players.update;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

public record PlayerToUpdate(
        @NotNull(message = "identifier is required") UUID identifier,
        @NotBlank(message = "firstName is required") String firstName,
        @NotBlank(message = "lastName is required") String lastName,
        @NotNull(message = "birthDate is required") @PastOrPresent(message = "birthDate must be in the past or present") LocalDate birthDate,
        @PositiveOrZero(message = "points must be positive or zero") int points) {
}
