package com.dyma.tennis.features.tournaments.get;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.features.tournaments.Tournament;
import com.dyma.tennis.features.tournaments.TournamentService;
import com.dyma.tennis.shared.error.Error;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tournaments API")
@RestController
@RequestMapping("/tournaments")
public class GetTournamentController {

        private final Logger log = LoggerFactory.getLogger(GetTournamentController.class);

        private final TournamentService tournamentService;

        public GetTournamentController(TournamentService tournamentService) {
                this.tournamentService = tournamentService;
        }

        @Operation(summary = "Finds a tournament", description = "Finds a tournament", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tournament", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = Tournament.class)) }),
                        @ApiResponse(responseCode = "404", description = "Tournament with specified identifier was not found.", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
                        @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action.")
        })
        @GetMapping("{identifier}")
        public Tournament getTournament(@PathVariable("identifier") UUID identifier) {
                return tournamentService.getByIdentifier(identifier);
        }
}
