package com.dyma.tennis.features.tournaments.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dyma.tennis.features.players.Player;
import com.dyma.tennis.features.players.PlayerService;

@SpringBootTest
public class RegistrationServiceIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PlayerService playerService;

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void shouldRegisterPlayerToTournament() {
        // Arrange
        UUID tournamentIdentifier = UUID.fromString("d4a9f8e2-9051-4739-90bc-1cb7e4c7ad42"); // French Open
        UUID playerIdentifier = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"); // Rafael Nadal

        // Act
        registrationService.register(tournamentIdentifier, playerIdentifier);

        // Assert
        Player updatedPlayer = playerService.getByIdentifier(playerIdentifier);
        assertThat(updatedPlayer.tournaments())
                .extracting("identifier")
                .contains(tournamentIdentifier);
    }

    @Test
    public void shouldFailToRegisterPlayer_WhenPlayerIsAlreadyRegistered() {
        // Arrange
        UUID tournamentIdentifier = UUID.fromString("d4a9f8e2-9051-4739-90bc-1cb7e4c7ad42"); // French Open
        UUID playerIdentifier = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"); // Rafael Nadal

        // Act & Assert
        registrationService.register(tournamentIdentifier, playerIdentifier);

        assertThrows(TournamentRegistrationException.class, () -> {
            registrationService.register(tournamentIdentifier, playerIdentifier);
        });
    }
}
