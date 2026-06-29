package com.dyma.tennis.features.tournaments.list;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.features.tournaments.Tournament;
import com.dyma.tennis.features.tournaments.TournamentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tournaments API")
@RestController
@RequestMapping("/tournaments")
public class ListTournamentsController {

    private final Logger log = LoggerFactory.getLogger(ListTournamentsController.class);

    private final TournamentService tournamentService;

    public ListTournamentsController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Operation(summary = "Finds tournaments", description = "Finds tournaments", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournaments list", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Tournament.class))) }),
            @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action.")
    })
    @GetMapping
    public List<Tournament> list() {
        return tournamentService.getAllTournaments();
    }
}
