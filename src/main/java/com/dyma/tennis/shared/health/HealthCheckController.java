package com.dyma.tennis.shared.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "HealthCheck")
@RestController
public class HealthCheckController {

    @Autowired
    private HealthCheckRepository healthCheckRepository;

    @Operation(summary = "Returns application status", description = "Returns the application status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HealthCheck status with some details", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = HealthCheck.class))
            })
    })

    @GetMapping("/healthcheck")
    public HealthCheck healthCheck() {
        Long activeSession = healthCheckRepository.countApplicationConnections();

        if (activeSession > 0) {
            return new HealthCheck(ApplicationStatus.OK, "Welcome to DynaApp");
        } else {
            return new HealthCheck(ApplicationStatus.KO, "Error with database connection");
        }
    }
}
