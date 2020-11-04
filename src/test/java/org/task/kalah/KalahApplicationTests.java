package org.task.kalah;

import org.task.kalah.dto.response.GameCreatedResponse;
import org.task.kalah.dto.response.GameSowStonesResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KalahApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldCreateGame() {
        //given
        String url = "http://localhost:" + port + "/games/";

        //when
        GameCreatedResponse body = restTemplate.postForObject(url, null,
                GameCreatedResponse.class);

        //then
        assertNotNull(body.getId());
        assertThat(body.getUri(), matchesPattern("http:\\/\\/localhost:[0-9]+\\/games\\/[0-9]+"));
    }

    @Test
    public void shouldSowStones() {
        //given
        String createGameUrl = "http://localhost:" + port + "/games/";

        GameCreatedResponse gameCreatedResponse = restTemplate.postForObject(createGameUrl, null,
                GameCreatedResponse.class);

        HttpEntity<GameSowStonesResponse> entity = new HttpEntity<>(null);

        int pitId = 1;

        String sowStonesUrl = gameCreatedResponse.getUri() + "/pits/" + pitId;

        //when
        ResponseEntity<GameSowStonesResponse> exchangeResult = restTemplate.exchange(sowStonesUrl, HttpMethod.PUT, entity, GameSowStonesResponse.class);

        //then
        GameSowStonesResponse body = exchangeResult.getBody();

        assertNotNull(body.getId());
        assertThat(body.getUri(), matchesPattern("http:\\/\\/localhost:[0-9]+\\/games\\/1"));

        Map<Integer, Integer> board = body.getStatus();
        assertEquals(board.get(1), 0);
        IntStream.rangeClosed(2, 6).forEach(value -> assertEquals(7, board.get(value)));
        assertEquals(board.get(7), 1);
        IntStream.rangeClosed(8, 13).forEach(value -> assertEquals(6, board.get(value)));
        assertEquals(board.get(14), 0);
    }
}

