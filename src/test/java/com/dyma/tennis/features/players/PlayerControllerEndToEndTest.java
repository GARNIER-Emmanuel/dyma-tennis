package com.dyma.tennis.features.players;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.dyma.tennis.features.players.create.PlayerToCreate;
import com.dyma.tennis.features.players.update.PlayerToUpdate;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class PlayerControllerEndToEndTest {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @BeforeEach
        void clearDatabase(@Autowired Flyway flyway) {
                flyway.clean();
                flyway.migrate();
        }

        @Test
        public void shouldCreatePlayer() {
                // Given
                PlayerToCreate playerToCreate = new PlayerToCreate(
                                "Carlos",
                                "Alcaraz",
                                LocalDate.of(2003, Month.MAY, 5),
                                4500);

                // When
                String url = "http://localhost:" + port + "/players";
                HttpEntity<PlayerToCreate> request = new HttpEntity<>(playerToCreate);
                ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request,
                                Player.class);

                // Then
                Assertions.assertThat(playerResponseEntity.getBody().info().lastName()).isEqualTo("Alcaraz");
                Assertions.assertThat(playerResponseEntity.getBody().info().rank().position()).isEqualTo(1);
        }

        @Test
        public void shouldFailToCreatePlayerWhenPlayerToCreateIsInvalid() {
                // Given
                UUID unknownPlayerIdentifier = UUID.fromString("1b2c3d4e-f5f6-7890-1234-567890abcdef");
                PlayerToUpdate playerToCreate = new PlayerToUpdate(
                                unknownPlayerIdentifier,
                                "Carlos",
                                null,
                                LocalDate.of(2003, Month.MAY, 5),
                                4500);

                // When
                String url = "http://localhost:" + port + "/players";
                HttpEntity<PlayerToUpdate> request = new HttpEntity<>(playerToCreate);
                ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request,
                                Player.class);

                // Then
                Assertions.assertThat(playerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldUpdatePlayer() {
                // Given
                UUID rafaelNadalIdentifier = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
                PlayerToUpdate playerToUpdate = new PlayerToUpdate(
                                rafaelNadalIdentifier,
                                "RafaelTest",
                                "Nadal",
                                LocalDate.of(2002, Month.JANUARY, 5),
                                3700);

                // When
                String url = "http://localhost:" + port + "/players";
                HttpEntity<PlayerToUpdate> request = new HttpEntity<>(playerToUpdate);
                ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.PUT, request,
                                Player.class);

                // Then
                Assertions.assertThat(playerResponseEntity.getBody().info().firstName()).isEqualTo("RafaelTest");
                Assertions.assertThat(playerResponseEntity.getBody().info().rank().position()).isEqualTo(2);
        }

        @Test
        public void shouldDeletePlayer() {
                // Given
                UUID rafaelNadalIdentifier = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
                // When
                String url = "http://localhost:" + port + "/players";
                this.restTemplate.exchange(url + "/" + rafaelNadalIdentifier, HttpMethod.DELETE, null, Void.class);
                ResponseEntity<List<Player>> playerResponseEntity = this.restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<List<Player>>() {
                                });
                // Then
                Assertions.assertThat(playerResponseEntity.getBody())
                                .extracting("info.lastName", "info.rank.position")
                                .containsExactly(Assertions.tuple("Djokovic", 1), Assertions.tuple("Federer", 2));
        }
}
