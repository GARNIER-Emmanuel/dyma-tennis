package com.dyma.tennis.features.tournaments.register;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class TournamentRegistrationController {

        private final Logger log = LoggerFactory.getLogger(TournamentRegistrationController.class);

        private final RegistrationService registrationService;

        public TournamentRegistrationController(RegistrationService registrationService) {
                this.registrationService = registrationService;
        }

        @Operation(summary = "Register a player to a tournament", description = "Register a player to a tournament", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Player successfully registered"),
                        @ApiResponse(responseCode = "400", description = "Registration request is invalid", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                        }),
                        @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action")
        })
        @PostMapping("{tournamentIdentifier}/players/{playerIdentifier}")
        public void register(
                        @PathVariable("tournamentIdentifier") UUID tournamentIdentifier,
                        @PathVariable("playerIdentifier") UUID playerToRegister) {
                log.info("Request to register player {} to tournament {}", playerToRegister, tournamentIdentifier);
                registrationService.register(tournamentIdentifier, playerToRegister);
        }
}
