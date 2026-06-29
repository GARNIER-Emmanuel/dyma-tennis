package com.dyma.tennis.features.tournaments.db;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {
    Optional<TournamentEntity> findOneByIdentifier(UUID identifier);

    Optional<TournamentEntity> findOneByNameIgnoreCase(String name);
}
