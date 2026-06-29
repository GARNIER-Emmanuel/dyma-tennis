package com.dyma.tennis.features.players.get;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.hamcrest.CoreMatchers;

import java.util.UUID;

import com.dyma.tennis.features.players.PlayerList;
import com.dyma.tennis.features.players.PlayerNotFoundException;
import com.dyma.tennis.features.players.PlayerService;

@WebMvcTest(controllers = GetPlayerController.class)
public class GetPlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @Test
    public void shouldRetrievePlayer() throws Exception {
        // Arrange
        UUID playerToRetrieve = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
        Mockito.when(playerService.getByIdentifier(playerToRetrieve))
                .thenReturn(PlayerList.RAFAEL_NADAL);

        // Act & Assert
        mockMvc.perform(get("/players/" + playerToRetrieve))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.firstName", CoreMatchers.is("Rafael")))
                .andExpect(jsonPath("$.info.rank.position", CoreMatchers.is(1)));
    }

    @Test
    public void shouldReturn404NotFoundWhenPlayerDoesNotExist() throws Exception {
        // Arrange
        UUID unknownPlayerToRetrieve = UUID.fromString("99999999-9999-9999-9999-999999999999");
        Mockito.when(playerService.getByIdentifier(unknownPlayerToRetrieve))
                .thenThrow(new PlayerNotFoundException(unknownPlayerToRetrieve));

        // Act & Assert
        mockMvc.perform(get("/players/" + unknownPlayerToRetrieve))
                .andExpect(status().isNotFound());
    }
}
