package com.dyma.tennis.features.players.create;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.PostMapping;
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
public class CreatePlayerController {

    private final Logger log = LoggerFactory.getLogger(CreatePlayerController.class);

    private final PlayerRepository playerRepository;
    private final RankingUpdater rankingUpdater;
    private final PlayerMapper playerMapper;

    public CreatePlayerController(PlayerRepository playerRepository, RankingUpdater rankingUpdater, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.rankingUpdater = rankingUpdater;
        this.playerMapper = playerMapper;
    }

    @Operation(summary = "Create a player", description = "Here we create a player", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New player", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PlayerToCreate.class))
            }),
            @ApiResponse(responseCode = "400", description = "Player already exists", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "This use is not authorized to perfom this action")
    })
    @PostMapping
    public Player createPlayer(@RequestBody @Valid PlayerToCreate playerToCreate) {
        log.info("Demande de creation du joueur {}", playerToCreate.lastName());
        try {
            Optional<PlayerEntity> player = playerRepository
                    .findOneByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(playerToCreate.firstName(),
                            playerToCreate.lastName(), playerToCreate.birthDate());

            if (player.isPresent()) {
                log.warn("Le joueur a cree avec first name {} et last name {} et birthdate {} existe deja",
                        playerToCreate.firstName(),
                        playerToCreate.lastName(), playerToCreate.birthDate());
                throw new PlayerAlreadyExistsException(playerToCreate.firstName(), playerToCreate.lastName(),
                        playerToCreate.birthDate());
            }

            PlayerEntity playerEntity = new PlayerEntity(
                    UUID.randomUUID(),
                    playerToCreate.firstName(),
                    playerToCreate.lastName(),
                    playerToCreate.birthDate(),
                    playerToCreate.points(),
                    9999999);

            playerRepository.save(playerEntity);

            rankingUpdater.updatePlayerRanking();

            // Fetch the created player entity to map it to Player DTO
            Optional<PlayerEntity> created = playerRepository.findOneByIdentifier(playerEntity.getIdentifier());
            if (created.isEmpty()) {
                throw new PlayerNotFoundException(playerEntity.getIdentifier());
            }

            return playerMapper.playerEntityToPlayer(created.get());

        } catch (DataAccessException e) {
            log.error("Erreur lors de la creation du joueur ", e);
            throw new PlayerDataRetrievalException(e);
        }
    }
}
