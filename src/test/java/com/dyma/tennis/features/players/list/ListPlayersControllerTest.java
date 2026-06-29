package com.dyma.tennis.features.players.list;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import org.hamcrest.CoreMatchers;

import java.util.Arrays;
import java.util.Collections;

import com.dyma.tennis.features.players.PlayerList;
import com.dyma.tennis.features.players.PlayerService;

@WebMvcTest(controllers = ListPlayersController.class)
public class ListPlayersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @Test
    public void shouldListAllPlayersSortedByRank() throws Exception {
        // Arrange
        Mockito.when(playerService.getAllPlayers()).thenReturn(Arrays.asList(
                PlayerList.NOVAK_DJOKOVIC,
                PlayerList.RAFAEL_NADAL,
                PlayerList.ROGER_FEDERER,
                PlayerList.ANDY_MURRAY
        ));

        // Act & Assert
        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].info.lastName", CoreMatchers.is("Djokovic")))
                .andExpect(jsonPath("$[1].info.lastName", CoreMatchers.is("Nadal")))
                .andExpect(jsonPath("$[2].info.lastName", CoreMatchers.is("Federer")))
                .andExpect(jsonPath("$[3].info.lastName", CoreMatchers.is("Murray")));
    }
}
