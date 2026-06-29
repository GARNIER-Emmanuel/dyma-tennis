package com.dyma.tennis.features.players.update;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.features.players.Player;
import com.dyma.tennis.features.players.PlayerMapper;
import com.dyma.tennis.features.players.PlayerAlreadyExistsException;
import com.dyma.tennis.features.players.PlayerDataRetrievalException;
import com.dyma.tennis.features.players.PlayerNotFoundException;
import com.dyma.tennis.features.players.db.PlayerEntity;
import com.dyma.tennis.features.players.db.PlayerRepository;
import com.dyma.tennis.features.players.ranking.RankingUpdater;
import com.dyma.tennis.shared.error.Error;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

@Tag(name = "Tennis Players")
@RestController
@RequestMapping("players")
public class UpdatePlayerController {

        private final Logger log = LoggerFactory.getLogger(UpdatePlayerController.class);

        private final PlayerRepository playerRepository;
        private final RankingUpdater rankingUpdater;
        private final PlayerMapper playerMapper;

        public UpdatePlayerController(PlayerRepository playerRepository, RankingUpdater rankingUpdater, PlayerMapper playerMapper) {
                this.playerRepository = playerRepository;
                this.rankingUpdater = rankingUpdater;
                this.playerMapper = playerMapper;
        }

        @Operation(summary = "Update a player", description = "Here we update a player", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Update player", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = PlayerToUpdate.class))
                        }),
                        @ApiResponse(responseCode = "404", description = "Player not found", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action")
        })
        @PutMapping
        public Player updatePlayer(@RequestBody @Valid PlayerToUpdate playerToUpdate) {
                log.info("Demande de mise a jour du joueur {}", playerToUpdate);
                try {
                        Optional<PlayerEntity> existingPlayer = playerRepository
                                        .findOneByIdentifier(playerToUpdate.identifier());

                        if (existingPlayer.isEmpty()) {
                                log.warn("Le joueur avec l'identifiant {} n'a pas été trouvé",
                                                playerToUpdate.identifier());
                                throw new PlayerNotFoundException(playerToUpdate.identifier());
                        }

                        Optional<PlayerEntity> potentiallyDupolicatedPlayer = playerRepository
                                        .findOneByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(
                                                        playerToUpdate.firstName(),
                                                        playerToUpdate.lastName(), playerToUpdate.birthDate());

                        if (potentiallyDupolicatedPlayer.isPresent()
                                        && !potentiallyDupolicatedPlayer.get().getIdentifier()
                                                        .equals(playerToUpdate.identifier())) {
                                log.warn("Le joueur avec le prenom {}, le nom {} et la date de naissance {} existe deja",
                                                playerToUpdate.firstName(), playerToUpdate.lastName(),
                                                playerToUpdate.birthDate());
                                throw new PlayerAlreadyExistsException(playerToUpdate.firstName(),
                                                playerToUpdate.lastName(),
                                                playerToUpdate.birthDate());
                        }

                        existingPlayer.get().setFirstName(playerToUpdate.firstName());
                        existingPlayer.get().setLastName(playerToUpdate.lastName());
                        existingPlayer.get().setBirthDate(playerToUpdate.birthDate());
                        existingPlayer.get().setPoints(playerToUpdate.points());

                        playerRepository.save(existingPlayer.get());

                        rankingUpdater.updatePlayerRanking();

                        Optional<PlayerEntity> updated = playerRepository
                                        .findOneByIdentifier(playerToUpdate.identifier());
                        if (updated.isEmpty()) {
                                throw new PlayerNotFoundException(playerToUpdate.identifier());
                        }

                        return playerMapper.playerEntityToPlayer(updated.get());

                } catch (DataAccessException e) {
                        log.error("Erreur lors de la mise a jour du joueur ", e);
                        throw new PlayerDataRetrievalException(e);
                }
        }
}
