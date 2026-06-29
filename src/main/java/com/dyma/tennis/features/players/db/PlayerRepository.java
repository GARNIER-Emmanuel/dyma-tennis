package com.dyma.tennis.features.players.db;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findOneByIdentifier(UUID identifier);

    Optional<PlayerEntity> findOneByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(String firstName,
            String lastName, LocalDate birthDate);

}
