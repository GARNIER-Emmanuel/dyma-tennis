package com.dyma.tennis.features.players.list;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.features.players.Player;
import com.dyma.tennis.features.players.PlayerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tennis Players")
@RestController
@RequestMapping("players")
public class ListPlayersController {

    private final Logger log = LoggerFactory.getLogger(ListPlayersController.class);

    private final PlayerService playerService;

    public ListPlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(summary = "Return Players", description = "Return The Players", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Player.class)))
            }),
            @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action")
    })
    @GetMapping
    public List<Player> list() {
        return playerService.getAllPlayers();
    }
}
