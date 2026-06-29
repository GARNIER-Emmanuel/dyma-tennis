package com.dyma.tennis.features.tournaments.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dyma.tennis.features.players.db.PlayerEntity;
import com.dyma.tennis.features.players.db.PlayerRepository;
import com.dyma.tennis.features.tournaments.db.TournamentEntity;
import com.dyma.tennis.features.tournaments.db.TournamentRepository;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerRepository playerRepository;

    private RegistrationService registrationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationService(tournamentRepository, playerRepository);
    }

    @Test
    public void shouldRegisterPlayerToTournament() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        TournamentEntity tournament = new TournamentEntity(tournamentId, "Roland Garros", LocalDate.now(), LocalDate.now().plusDays(7), 1000, 4);
        PlayerEntity player = new PlayerEntity(playerId, "Rafael", "Nadal", LocalDate.of(1986, 6, 3), 10000, 1);

        when(tournamentRepository.findOneByIdentifier(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerRepository.findOneByIdentifier(playerId)).thenReturn(Optional.of(player));

        // Act
        registrationService.register(tournamentId, playerId);

        // Assert
        verify(playerRepository).save(player);
        assertThat(player.getTournaments()).contains(tournament);
    }

    @Test
    public void shouldFailToRegister_WhenTournamentIsNotFound() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        when(tournamentRepository.findOneByIdentifier(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(TournamentRegistrationException.class, () -> {
            registrationService.register(tournamentId, playerId);
        });
        assertThat(exception.getMessage()).contains("Tournament with identifier");
    }

    @Test
    public void shouldFailToRegister_WhenPlayerIsNotFound() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        TournamentEntity tournament = new TournamentEntity(tournamentId, "Roland Garros", LocalDate.now(), LocalDate.now().plusDays(7), 1000, 4);

        when(tournamentRepository.findOneByIdentifier(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerRepository.findOneByIdentifier(playerId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(TournamentRegistrationException.class, () -> {
            registrationService.register(tournamentId, playerId);
        });
        assertThat(exception.getMessage()).contains("Player with identifier");
    }
}
