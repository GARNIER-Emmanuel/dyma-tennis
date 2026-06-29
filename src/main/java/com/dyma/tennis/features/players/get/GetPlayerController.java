package com.dyma.tennis.features.players.get;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.features.players.Player;
import com.dyma.tennis.features.players.PlayerService;
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
public class GetPlayerController {

        private final Logger log = LoggerFactory.getLogger(GetPlayerController.class);

        private final PlayerService playerService;

        public GetPlayerController(PlayerService playerService) {
                this.playerService = playerService;
        }

        @Operation(summary = "Get player by name", description = "Here we get a player by his name", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Player", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = Player.class))
                        }),
                        @ApiResponse(responseCode = "404", description = "Player with that identifier not found", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "This user is not authorized to perfom this action")
        })
        @GetMapping("{identifier}")
        public Player getByIdentifier(@PathVariable("identifier") UUID identifier) {
                return playerService.getByIdentifier(identifier);
        }
}
