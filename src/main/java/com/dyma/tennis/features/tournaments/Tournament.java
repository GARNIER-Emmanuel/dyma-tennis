package com.dyma.tennis.features.tournaments;

import java.util.Set;

import jakarta.validation.Valid;

import com.dyma.tennis.features.players.PlayerDescription;

public record Tournament(
        @Valid TournamentDescription info,
        @Valid Set<PlayerDescription> players
) {}
