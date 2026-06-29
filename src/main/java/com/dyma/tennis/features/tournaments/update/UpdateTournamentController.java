package com.dyma.tennis.features.tournaments.update;

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

import com.dyma.tennis.features.tournaments.Tournament;
import com.dyma.tennis.features.tournaments.TournamentMapper;
import com.dyma.tennis.features.tournaments.TournamentAlreadyExistsException;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Tag(name = "Tournaments API")
@RestController
@RequestMapping("/tournaments")
public class UpdateTournamentController {

    private final Logger log = LoggerFactory.getLogger(UpdateTournamentController.class);

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    public UpdateTournamentController(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
    }

    @Operation(summary = "Updates a tournament", description = "Updates a tournament", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated tournament", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentToUpdate.class)) }),
            @ApiResponse(responseCode = "404", description = "Tournament with specified identifier was not found.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
            @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action.")
    })
    @PutMapping
    public Tournament updateTournament(@RequestBody @Valid TournamentToUpdate tournamentToUpdate) {
        log.info("Invoking update with tournamentToUpdate={}", tournamentToUpdate);
        try {
            Optional<TournamentEntity> existingTournament = tournamentRepository
                    .findOneByIdentifier(tournamentToUpdate.identifier());
            if (existingTournament.isEmpty()) {
                log.warn("Could not find tournament to update with identifier={}", tournamentToUpdate.identifier());
                throw new TournamentNotFoundException(tournamentToUpdate.identifier());
            }

            Optional<TournamentEntity> potentiallyDuplicatedTournament = tournamentRepository
                    .findOneByNameIgnoreCase(tournamentToUpdate.name());
            if (potentiallyDuplicatedTournament.isPresent()
                    && !potentiallyDuplicatedTournament.get().getIdentifier().equals(tournamentToUpdate.identifier())) {
                log.warn("Tournament to update with name={} already exists", tournamentToUpdate.name());
                throw new TournamentAlreadyExistsException(tournamentToUpdate.name());
            }

            existingTournament.get().setName(tournamentToUpdate.name());
            existingTournament.get().setStartDate(tournamentToUpdate.startDate());
            existingTournament.get().setEndDate(tournamentToUpdate.endDateDate());
            existingTournament.get().setPrizeMoney(tournamentToUpdate.prizeMoney());
            existingTournament.get().setCapacity(tournamentToUpdate.capacity());

            tournamentRepository.save(existingTournament.get());

            Optional<TournamentEntity> updated = tournamentRepository
                    .findOneByIdentifier(tournamentToUpdate.identifier());
            if (updated.isEmpty()) {
                throw new TournamentNotFoundException(tournamentToUpdate.identifier());
            }

            return tournamentMapper.tournamentEntityToTournament(updated.get());

        } catch (DataAccessException e) {
            log.error("Could not update tournament={}", tournamentToUpdate, e);
            throw new TournamentDataRetrievalException(e);
        }
    }
}
