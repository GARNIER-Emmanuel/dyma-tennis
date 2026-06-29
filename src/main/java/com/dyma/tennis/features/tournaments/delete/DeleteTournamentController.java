package com.dyma.tennis.features.tournaments.delete;

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

import com.dyma.tennis.features.tournaments.TournamentDataRetrievalException;
import com.dyma.tennis.features.tournaments.TournamentNotFoundException;
import com.dyma.tennis.features.tournaments.db.TournamentEntity;
import com.dyma.tennis.features.tournaments.db.TournamentRepository;
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
public class DeleteTournamentController {

    private final Logger log = LoggerFactory.getLogger(DeleteTournamentController.class);

    private final TournamentRepository tournamentRepository;

    public DeleteTournamentController(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Operation(summary = "Deletes a tournament", description = "Deletes a tournament", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournament has been deleted"),
            @ApiResponse(responseCode = "404", description = "Tournament with specified identifier was not found.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
            @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action.")
    })
    @DeleteMapping("{identifier}")
    public void deleteTournament(@PathVariable("identifier") UUID identifier) {
        log.info("Invoking delete with identifier={}", identifier);
        try {
            Optional<TournamentEntity> tournamentToDelete = tournamentRepository.findOneByIdentifier(identifier);
            if (tournamentToDelete.isEmpty()) {
                log.warn("Could not find tournament to delete with identifier={}", identifier);
                throw new TournamentNotFoundException(identifier);
            }

            tournamentRepository.delete(tournamentToDelete.get());
        } catch (DataAccessException e) {
            log.error("Could not delete tournament with identifier={}", identifier, e);
            throw new TournamentDataRetrievalException(e);
        }
    }
}
