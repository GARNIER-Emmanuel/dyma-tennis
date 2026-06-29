package com.dyma.tennis.features.tournaments.create;

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
public class CreateTournamentController {

    private final Logger log = LoggerFactory.getLogger(CreateTournamentController.class);

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    public CreateTournamentController(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
    }

    @Operation(summary = "Creates a tournament", description = "Creates a tournament", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created tournament", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentToCreate.class)) }),
            @ApiResponse(responseCode = "400", description = "Tournament already exists.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
            @ApiResponse(responseCode = "403", description = "This user is not authorized to perform this action.")
    })
    @PostMapping
    public Tournament createTournament(@RequestBody @Valid TournamentToCreate tournamentToCreate) {
        log.info("Invoking create with tournamentToCreate={}", tournamentToCreate);

        Optional<TournamentEntity> tournament = tournamentRepository.findOneByNameIgnoreCase(tournamentToCreate.name());
        if (tournament.isPresent()) {
            log.warn("Tournament to create with name={} already exists", tournamentToCreate.name());
            throw new TournamentAlreadyExistsException(tournamentToCreate.name());
        }

        try {
            TournamentEntity tournamentToRegister = new TournamentEntity(
                    UUID.randomUUID(),
                    tournamentToCreate.name(),
                    tournamentToCreate.startDate(),
                    tournamentToCreate.endDateDate(),
                    tournamentToCreate.prizeMoney(),
                    tournamentToCreate.capacity());

            TournamentEntity registeredTournament = tournamentRepository.save(tournamentToRegister);

            Optional<TournamentEntity> created = tournamentRepository
                    .findOneByIdentifier(registeredTournament.getIdentifier());
            if (created.isEmpty()) {
                throw new TournamentNotFoundException(registeredTournament.getIdentifier());
            }

            return tournamentMapper.tournamentEntityToTournament(created.get());

        } catch (DataAccessException e) {
            log.error("Could not create tournament={}", tournamentToCreate, e);
            throw new TournamentDataRetrievalException(e);
        }
    }
}
