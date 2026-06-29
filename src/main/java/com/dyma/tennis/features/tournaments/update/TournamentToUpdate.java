package com.dyma.tennis.features.tournaments.update;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TournamentToUpdate(
        @NotNull(message = "identifier is required") UUID identifier,
        @NotBlank(message = "Name is mandatory") String name,
        @NotNull(message = "Start date is mandatory") LocalDate startDate,
        @NotNull(message = "End date is mandatory") LocalDate endDateDate,
        @Positive(message = "Prize money must be positive") Integer prizeMoney,
        @NotNull(message = "Capacity is mandatory") @Positive(message = "Capacity must be positive") Integer capacity
) {
}
