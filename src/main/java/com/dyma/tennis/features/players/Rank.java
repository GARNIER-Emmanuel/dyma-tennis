package com.dyma.tennis.features.players;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record Rank(
        @Positive(message = "Rank must be positive") int position,
        @PositiveOrZero(message = "Points must be positive or zero") int points) {
}
