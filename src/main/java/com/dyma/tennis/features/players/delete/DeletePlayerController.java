package com.dyma.tennis.features.players.delete;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@Tag(name = "Tennis Players")
@RestController
@RequestMapping("players")
public class DeletePlayerController {

    private final Logger log = LoggerFactory.getLogger(DeletePlayerController.class);

    private final PlayerRepository playerRepository;
    private final RankingUpdater rankingUpdater;

    public DeletePlayerController(PlayerRepository playerRepository, RankingUpdater rankingUpdater) {
        this.playerRepository = playerRepository;
        this.rankingUpdater = rankingUpdater;
    }

    @Operation(summary = "Delete a player", description = "Here we delete a player", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete player"),
            @ApiResponse(responseCode = "404", description = "Player not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "403", description = "This user is not authorized to perfom this action")
    })
    @DeleteMapping("{identifier}")
    public void deletePlayer(@PathVariable("identifier") UUID identifier) {
        log.info("Demande de suppression du joueur {}", identifier);
        try {
            Optional<PlayerEntity> playerToDelete = playerRepository.findOneByIdentifier(identifier);
            if (playerToDelete.isEmpty()) {
                log.warn("Le joueur avec l'identifiant {} n'a pas été trouvé", identifier);
                throw new PlayerNotFoundException(identifier);
            }
            playerRepository.delete(playerToDelete.get());

            rankingUpdater.updatePlayerRanking();
        } catch (DataAccessException e) {
            log.error("Erreur lors de la suppression du joueur ", e);
            throw new PlayerDataRetrievalException(e);
        }
    }
}
