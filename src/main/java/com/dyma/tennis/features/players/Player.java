package com.dyma.tennis.features.players;

import java.util.Set;

import jakarta.validation.Valid;

import com.dyma.tennis.features.tournaments.TournamentDescription;

public record Player(
        @Valid PlayerDescription info,
        @Valid Set<TournamentDescription> tournaments
) {}
