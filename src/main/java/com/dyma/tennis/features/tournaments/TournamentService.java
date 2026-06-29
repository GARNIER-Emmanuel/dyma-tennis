package com.dyma.tennis.features.tournaments;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.dyma.tennis.features.tournaments.db.TournamentEntity;
import com.dyma.tennis.features.tournaments.db.TournamentRepository;

@Service
public class TournamentService {

    private final Logger log = LoggerFactory.getLogger(TournamentService.class);
    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    public TournamentService(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
    }

    public List<Tournament> getAllTournaments() {
        log.info("Invoking getAllTournaments()");
        try {
            return tournamentRepository.findAll().stream()
                    .map(tournamentMapper::tournamentEntityToTournament)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Could not retrieve tournaments", e);
            throw new TournamentDataRetrievalException(e);
        }
    }

    public Tournament getByIdentifier(UUID identifier) {
        log.info("Invoking getByIdentifier with identifier={}", identifier);
        try {
            Optional<TournamentEntity> tournament = tournamentRepository.findOneByIdentifier(identifier);
            if (tournament.isEmpty()) {
                log.warn("Could not find tournament with identifier={}", identifier);
                throw new TournamentNotFoundException(identifier);
            }
            return tournamentMapper.tournamentEntityToTournament(tournament.get());
        } catch (DataAccessException e) {
            log.error("Could not find tournament with identifier={}", identifier, e);
            throw new TournamentDataRetrievalException(e);
        }
    }
}
