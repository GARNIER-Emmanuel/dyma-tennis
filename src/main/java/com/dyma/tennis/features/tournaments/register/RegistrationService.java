package com.dyma.tennis.features.tournaments.register;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.dyma.tennis.features.players.PlayerDataRetrievalException;
import com.dyma.tennis.features.players.db.PlayerEntity;
import com.dyma.tennis.features.players.db.PlayerRepository;
import com.dyma.tennis.features.tournaments.db.TournamentEntity;
import com.dyma.tennis.features.tournaments.db.TournamentRepository;

@Service
public class RegistrationService {

    private final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    public RegistrationService(TournamentRepository tournamentRepository, PlayerRepository playerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
    }

    public void register(UUID tournamentIdentifier, UUID playerToRegister) {
        log.info("Inscription du joueur {} au tournoi {}", playerToRegister, tournamentIdentifier);

        Optional<TournamentEntity> existingTournament;
        try {
            existingTournament = tournamentRepository.findOneByIdentifier(tournamentIdentifier);
        } catch (DataAccessException e) {
            log.error("Erreur lors de la recuperation du tournoi {}", tournamentIdentifier, e);
            throw new PlayerDataRetrievalException(e);
        }

        if (existingTournament.isEmpty()) {
            log.warn("Le tournoi {} n'existe pas", tournamentIdentifier);
            throw new TournamentRegistrationException("Tournament with identifier " + tournamentIdentifier + " not found");
        }

        TournamentEntity tournament = existingTournament.get();
        if (tournament.isFull()) {
            log.warn("Le tournoi {} est plein", tournamentIdentifier);
            throw new TournamentRegistrationException("Tournament with identifier " + tournamentIdentifier + " is full");
        }

        Optional<PlayerEntity> existingPlayer;
        try {
            existingPlayer = playerRepository.findOneByIdentifier(playerToRegister);
        } catch (DataAccessException e) {
            log.error("Erreur lors de la recuperation du joueur {}", playerToRegister, e);
            throw new PlayerDataRetrievalException(e);
        }

        if (existingPlayer.isEmpty()) {
            log.warn("Le joueur {} n'existe pas", playerToRegister);
            throw new TournamentRegistrationException("Player with identifier " + playerToRegister + " not found");
        }

        PlayerEntity player = existingPlayer.get();
        if (tournament.hasPlayer(player)) {
            log.warn("Le joueur {} est deja inscrit au tournoi {}", playerToRegister, tournamentIdentifier);
            throw new TournamentRegistrationException("Player with identifier " + playerToRegister + " is already registered to tournament " + tournamentIdentifier);
        }

        player.addTournament(tournament);
        try {
            playerRepository.save(player);
        } catch (DataAccessException e) {
            log.error("Erreur lors de l'enregistrement de l'inscription du joueur {} au tournoi {}", playerToRegister, tournamentIdentifier, e);
            throw new PlayerDataRetrievalException(e);
        }
    }
}
