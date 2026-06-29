package com.dyma.tennis.features.players;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record PlayerDescription(
        @NotNull(message = "Identifier is required") UUID identifier,
        @NotBlank(message = "FirstName is required") String firstName,
        @NotBlank(message = "LastName is required") String lastName,
        @NotNull(message = "Birth date is required") @PastOrPresent(message = "Birth date must be in the past or present") LocalDate birthDate,
        @Valid Rank rank
) {}
