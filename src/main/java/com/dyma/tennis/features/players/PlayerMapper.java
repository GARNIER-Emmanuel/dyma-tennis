package com.dyma.tennis.features.players;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dyma.tennis.features.players.db.PlayerEntity;
import com.dyma.tennis.features.tournaments.TournamentDescription;

@Component
public class PlayerMapper {

    public Player playerEntityToPlayer(PlayerEntity playerEntity) {
        PlayerDescription info = new PlayerDescription(
                playerEntity.getIdentifier(),
                playerEntity.getFirstName(),
                playerEntity.getLastName(),
                playerEntity.getBirthDate(),
                new Rank(playerEntity.getRank(), playerEntity.getPoints())
        );

        Set<TournamentDescription> tournaments = playerEntity.getTournaments() == null ? Collections.emptySet() :
                playerEntity.getTournaments().stream()
                        .map(t -> new TournamentDescription(
                                t.getIdentifier(),
                                t.getName(),
                                t.getStartDate(),
                                t.getEndDate(),
                                t.getPrizeMoney(),
                                t.getCapacity()
                        ))
                        .collect(Collectors.toSet());

        return new Player(info, tournaments);
    }
}
