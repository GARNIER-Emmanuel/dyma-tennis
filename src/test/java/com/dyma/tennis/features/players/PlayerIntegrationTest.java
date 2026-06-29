package com.dyma.tennis.features.players;

import java.time.Month;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import com.dyma.tennis.features.players.create.CreatePlayerController;
import com.dyma.tennis.features.players.create.PlayerToCreate;
import com.dyma.tennis.features.players.delete.DeletePlayerController;
import com.dyma.tennis.features.players.get.GetPlayerController;
import com.dyma.tennis.features.players.list.ListPlayersController;
import com.dyma.tennis.features.players.update.PlayerToUpdate;
import com.dyma.tennis.features.players.update.UpdatePlayerController;

@SpringBootTest
public class PlayerIntegrationTest {

    @Autowired
    private CreatePlayerController createPlayerController;

    @Autowired
    private GetPlayerController getPlayerController;

    @Autowired
    private ListPlayersController listPlayersController;

    @Autowired
    private UpdatePlayerController updatePlayerController;

    @Autowired
    private DeletePlayerController deletePlayerController;

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void shouldCreatePlayer() {
        // Arrange
        PlayerToCreate playerToSave = new PlayerToCreate(
                "john",
                "doe",
                LocalDate.of(2002, Month.JANUARY, 2),
                12999);
        // Act
        Player savedPlayer = createPlayerController.createPlayer(playerToSave);
        Player createdPlayer = getPlayerController.getByIdentifier(savedPlayer.info().identifier());
        // Assert
        Assertions.assertThat(createdPlayer.info().firstName()).isEqualTo("john");
        Assertions.assertThat(createdPlayer.info().lastName()).isEqualTo("doe");
        Assertions.assertThat(createdPlayer.info().birthDate()).isEqualTo(LocalDate.of(2002, Month.JANUARY, 2));
        Assertions.assertThat(createdPlayer.info().rank().position()).isEqualTo(1);
    }

    @Test
    public void shouldFailToCreateAnExistingPlayer() {
        // Arrange
        PlayerToCreate playerToSave = new PlayerToCreate(
                "john",
                "doe",
                LocalDate.of(2002, Month.JANUARY, 2),
                12999);

        createPlayerController.createPlayer(playerToSave);
        PlayerToCreate duplicatedPlayerToCreate = new PlayerToCreate(
                "john",
                "doe",
                LocalDate.of(2002, Month.JANUARY, 2),
                12999);
        // Act & Assert
        Exception exception = assertThrows(PlayerAlreadyExistsException.class, () -> {
            createPlayerController.createPlayer(duplicatedPlayerToCreate);
        });
        Assertions.assertThat(exception.getMessage())
                .isEqualTo(
                        "Player with this firstName : john and lastName : doe and birthDate : 2002-01-02 already exists");
    }

    @Test
    public void shouldUpdatePlayer() {
        // Arrange
        UUID nadalIdentifier = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
        PlayerToUpdate playerToSave = new PlayerToUpdate(
                nadalIdentifier,
                "RafaelTest",
                "Nadal",
                LocalDate.of(2002, Month.JANUARY, 5),
                3700);
        // Act
        updatePlayerController.updatePlayer(playerToSave);
        Player updatedPlayer = getPlayerController.getByIdentifier(nadalIdentifier);
        // Assert
        Assertions.assertThat(updatedPlayer.info().rank().position()).isEqualTo(2);
    }

    @Test
    public void shouldDeletePlayer() {
        // Arrange
        UUID playerToDelete = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
        // Act
        deletePlayerController.deletePlayer(playerToDelete);

        List<Player> allPlayers = listPlayersController.list();

        // Assert
        Assertions.assertThat(allPlayers)
                .extracting("info.lastName", "info.rank.position")
                .containsExactly(Assertions.tuple("Djokovic", 1), Assertions.tuple("Federer", 2));
    }

    @Test
    public void shouldFailToDeletePlayerWhenPlayerDoesNotExist() {
        // Arrange
        UUID unknownPlayer = UUID.fromString("99999999-9999-9999-9999-999999999999");
        // Acts
        Exception exception = assertThrows(PlayerNotFoundException.class, () -> {
            deletePlayerController.deletePlayer(unknownPlayer);
        });
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Player with this identifier : " + unknownPlayer + " does not exist");
    }
}
