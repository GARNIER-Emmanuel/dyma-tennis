package com.dyma.tennis.features.tournaments;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TournamentDescription(
        @NotNull(message = "Identifier is required") UUID identifier,
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Start date is required") LocalDate startDate,
        @NotNull(message = "End date is required") LocalDate endDate,
        @Positive(message = "Prize money must be positive") Integer prizeMoney,
        @NotNull(message = "Capacity is required") @Positive(message = "Capacity must be positive") Integer capacity
) {}
