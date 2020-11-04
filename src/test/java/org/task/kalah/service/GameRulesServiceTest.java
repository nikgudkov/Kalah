package org.task.kalah.service;

import org.task.kalah.dto.Player;
import org.task.kalah.exception.EmptyPitException;
import org.task.kalah.exception.WrongPitException;
import org.task.config.GameMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameRulesServiceTest {

    private GameRulesService gameRulesService;

    private GameMock game;

    @BeforeEach
    public void setUp() {
        gameRulesService = new GameRulesService();
        game = new GameMock(1L, 6, 6);
        game.initialize();

    }

    @ParameterizedTest
    @MethodSource("changeTurnDataProvider")
    void testTurnChange(int pitId, int stones, Player currentPlayer, Player expectedPlayer) {
        //given
        game.setCurrentPlayer(currentPlayer);
        game.getPits().put(pitId, stones);

        //when
        gameRulesService.sowStones(pitId, game);

        //then
        assertEquals(expectedPlayer, game.getCurrentPlayer());
    }

    private static Stream<Arguments> changeTurnDataProvider() {
        return Stream.of(
                Arguments.of(1, 6, Player.FIRST, Player.FIRST),
                Arguments.of(2, 6, Player.FIRST, Player.SECOND),
                Arguments.of(8, 6, Player.SECOND, Player.SECOND),
                Arguments.of(9, 6, Player.SECOND, Player.FIRST),
                // check when we end up in opposite player's Store
                Arguments.of(1, 13, Player.FIRST, Player.SECOND),
                Arguments.of(8, 13, Player.SECOND, Player.FIRST)
        );
    }


    @ParameterizedTest
    @DisplayName("Take opposite pit stones when one stone left on the current pit and don't change turn")
    @MethodSource("takeOppositePitStonesDataProvider")
    void shouldTakeOppositePitStonesWhenOneStoneLeftOnTheCurrentPit(int pitId, int stones, int expectedStockStonesCount, int playerStoreId,
                                                                    Player currentPlayer, Player expectedPlayer) {
        //given
        game.setCurrentPlayer(currentPlayer);
        Map<Integer, Integer> pits = game.getPits();
        pits.put(pitId, stones);
        pits.put(pitId + stones, 0);

        //when
        gameRulesService.sowStones(pitId, game);

        //then
        assertEquals(expectedPlayer, game.getCurrentPlayer());
        assertEquals(expectedStockStonesCount, pits.get(playerStoreId));
    }

    private static Stream<Arguments> takeOppositePitStonesDataProvider() {
        return Stream.of(
//                Arguments.of(1, 1, 7, 7, Player.FIRST, Player.FIRST),
                Arguments.of(8, 1, 7, 14, Player.SECOND, Player.SECOND)
        );
    }

    @ParameterizedTest
    @MethodSource("nonExistingPitIdProvider")
    void shouldNotSowFromNonExistingPit(int pitId) {
        //given

        //when
        WrongPitException exception = assertThrows(WrongPitException.class, () -> gameRulesService.sowStones(pitId, game));

        //then
        assertEquals("Pit does not exist", exception.getMessage());
    }

    private static Stream<Arguments> nonExistingPitIdProvider() {
        return Stream.of(
                Arguments.of(100),
                Arguments.of(-1),
                Arguments.of(0)
        );
    }


    @ParameterizedTest
    @MethodSource("storePitIdProvider")
    void shouldNotSowFromTheStore(int pitId) {
        //given

        //when
        WrongPitException exception = assertThrows(WrongPitException.class, () -> gameRulesService.sowStones(pitId, game));

        //then
        assertEquals("Can't sow from the store", exception.getMessage());
    }

    private static Stream<Arguments> storePitIdProvider() {
        return Stream.of(
                Arguments.of(7),
                Arguments.of(14)
        );
    }

    @ParameterizedTest
    @MethodSource("opponentPitDataProvider")
    void shouldNotSowsFromOpponentsPit(int pitId, Player currentPlayer) {
        //given
        game.setCurrentPlayer(currentPlayer);

        //when
        WrongPitException exception = assertThrows(WrongPitException.class, () -> gameRulesService.sowStones(pitId, game));

        //then
        assertEquals("Can't sow from opponent's pit", exception.getMessage());
    }

    private static Stream<Arguments> opponentPitDataProvider() {
        return Stream.concat(IntStream.rangeClosed(1, 6).mapToObj(value -> Arguments.of(value, Player.SECOND)),
                IntStream.rangeClosed(8, 13).mapToObj(value -> Arguments.of(value, Player.FIRST)));
    }

    @Test
    void shouldNotSowsFromEmptyPit() {
        //given
        int pitId = 1;
        game.setPits(new HashMap<>(Map.of(pitId, 0)));

        //when
        EmptyPitException exception = assertThrows(EmptyPitException.class, () -> gameRulesService.sowStones(pitId, game));
        //then
        assertEquals("Can't sow from an empty pit", exception.getMessage());
    }


    @ParameterizedTest
    @MethodSource("endGameDataProvider")
    void shouldEndGame(int pitId, int stones) {
        //given
        IntStream.rangeClosed(1, 5).forEach(id -> game.getPits().put(id, 0));
        game.getPits().put(pitId, stones);

        //when
        gameRulesService.sowStones(pitId, game);
        //then
    }

    private static Stream<Arguments> endGameDataProvider() {
        return Stream.of(
                Arguments.of(6, 1),
                Arguments.of(6, 2)
        );
    }


}