package com.dyma.tennis.features.tournaments.list;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.dyma.tennis.features.tournaments.TournamentList;
import com.dyma.tennis.features.tournaments.TournamentService;

@WebMvcTest(controllers = ListTournamentsController.class)
public class ListTournamentsControllerTest {

        @Autowired
        private MockMvcTester mockMvc;

        @MockitoBean
        private TournamentService tournamentService;

        @Test
        public void shouldListAllTournaments() {
                // Given
                Mockito.when(tournamentService.getAllTournaments()).thenReturn(TournamentList.ALL);

                // When
                var response = mockMvc.get().uri("/tournaments")
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange();

                // Then
                var json = response.assertThat().hasStatus(HttpStatus.OK).bodyJson();
                json.extractingPath("$.length()").isEqualTo(4);
                json.extractingPath("$[0].info.name").isEqualTo("Australian Open");
                json.extractingPath("$[1].info.name").isEqualTo("French Open");
                json.extractingPath("$[2].info.name").isEqualTo("Wimbledon");
                json.extractingPath("$[3].info.name").isEqualTo("US Open");
        }
}
