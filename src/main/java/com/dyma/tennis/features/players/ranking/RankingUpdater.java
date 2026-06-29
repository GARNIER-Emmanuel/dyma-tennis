package com.dyma.tennis.features.players.ranking;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dyma.tennis.features.players.db.PlayerEntity;
import com.dyma.tennis.features.players.db.PlayerRepository;

@Service
public class RankingUpdater {

    private final Logger log = LoggerFactory.getLogger(RankingUpdater.class);

    @Autowired
    private PlayerRepository playerRepository;

    public void updatePlayerRanking() {
        log.info("Mise a jour du classement");
        RankingCalculator rankingCalculator = new RankingCalculator(playerRepository.findAll());
        List<PlayerEntity> updatedPlayers = rankingCalculator.getNewPlayersRanking();
        playerRepository.saveAll(updatedPlayers);
    }
}
