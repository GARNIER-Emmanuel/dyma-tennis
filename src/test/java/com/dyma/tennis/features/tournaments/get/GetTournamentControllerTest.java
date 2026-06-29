package com.dyma.tennis.features.tournaments.get;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.dyma.tennis.features.tournaments.TournamentList;
import com.dyma.tennis.features.tournaments.TournamentNotFoundException;
import com.dyma.tennis.features.tournaments.TournamentService;

import java.util.UUID;

@WebMvcTest(controllers = GetTournamentController.class)
public class GetTournamentControllerTest {

        @Autowired
        private MockMvcTester mockMvc;

        @MockitoBean
        private TournamentService tournamentService;

        @Test
        public void shouldRetrieveTournament() {
                // Given
                UUID tournamentToRetrieve = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12");
                Mockito.when(tournamentService.getByIdentifier(tournamentToRetrieve))
                                .thenReturn(TournamentList.FRENCH_OPEN);

                // When
                var response = mockMvc.get().uri("/tournaments/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12")
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange();

                // Then
                var json = response.assertThat().hasStatus(HttpStatus.OK).bodyJson();
                json.extractingPath("$.info.name").isEqualTo("French Open");
        }

        @Test
        public void shouldReturn404NotFound_WhenTournamentDoesNotExist() {
                // Given
                UUID tournamentToRetrieve = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12");
                Mockito.when(tournamentService.getByIdentifier(tournamentToRetrieve))
                                .thenThrow(new TournamentNotFoundException(tournamentToRetrieve));

                // When
                var response = mockMvc.get().uri("/tournaments/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12")
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange();

                // Then
                var json = response.assertThat().hasStatus(HttpStatus.NOT_FOUND).bodyJson();
                json.extractingPath("$.errorDetails")
                                .isEqualTo("Tournament with identifier a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12 could not be found.");
        }
}
