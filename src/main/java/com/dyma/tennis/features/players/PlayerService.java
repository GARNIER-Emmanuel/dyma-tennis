package com.dyma.tennis.features.players;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.dyma.tennis.features.players.db.PlayerEntity;
import com.dyma.tennis.features.players.db.PlayerRepository;

@Service
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerService(PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    public List<Player> getAllPlayers() {
        log.info("Demande de tous les joueurs");
        try {
            return playerRepository.findAll().stream()
                    .map(playerMapper::playerEntityToPlayer)
                    .sorted(Comparator.comparing(player -> player.info().rank().position()))
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Erreur lors de la recuperation des joueurs");
            throw new PlayerDataRetrievalException(e);
        }
    }

    public Player getByIdentifier(UUID identifier) {
        log.info("Demande du joueur {}", identifier);
        try {
            Optional<PlayerEntity> playerEntityOptional = playerRepository.findOneByIdentifier(identifier);

            if (playerEntityOptional.isEmpty()) {
                log.warn("Le joueur {} n'a pas été trouvé", identifier);
                throw new PlayerNotFoundException(identifier);
            }

            return playerMapper.playerEntityToPlayer(playerEntityOptional.get());
        } catch (DataAccessException e) {
            log.error("Erreur lors de l'identification du joueur " + identifier, e);
            throw new PlayerDataRetrievalException(e);
        }
    }
}
