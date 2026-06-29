package com.dyma.tennis.features.tournaments;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dyma.tennis.features.players.PlayerDescription;
import com.dyma.tennis.features.players.Rank;
import com.dyma.tennis.features.tournaments.db.TournamentEntity;

@Component
public class TournamentMapper {

    public Tournament tournamentEntityToTournament(TournamentEntity tournamentEntity) {
        TournamentDescription info = new TournamentDescription(
                tournamentEntity.getIdentifier(),
                tournamentEntity.getName(),
                tournamentEntity.getStartDate(),
                tournamentEntity.getEndDate(),
                tournamentEntity.getPrizeMoney(),
                tournamentEntity.getCapacity()
        );

        Set<PlayerDescription> players = tournamentEntity.getPlayers() == null ? Collections.emptySet() :
                tournamentEntity.getPlayers().stream()
                        .map(p -> new PlayerDescription(
                                p.getIdentifier(),
                                p.getFirstName(),
                                p.getLastName(),
                                p.getBirthDate(),
                                new Rank(p.getRank(), p.getPoints())
                        ))
                        .collect(Collectors.toSet());

        return new Tournament(info, players);
    }
}
