package org.task.kalah.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Game {

    private final int INITIAL_STORE_STONES_COUNT = 0;

    private final Long id;
    private final int playerInitialPitStonesCount;
    private final int playerPitsCount;

    protected GameStatus gameStatus = GameStatus.NOT_STARTED;
    protected GameResult gameResult;
    protected Player currentPlayer = Player.FIRST;
    protected Map<Integer, Integer> pits = new ConcurrentHashMap<>();

    /**
     * Initializes the game.
     * Fills the pits with the stones.
     */
    public synchronized void initialize() {
        if (gameStatus == GameStatus.NOT_STARTED) {
            for (int index = 1; index <= (playerPitsCount + 1) * 2; index++) {
                //Populate Store pit
                if (index % (playerPitsCount + 1) == 0) {
                    pits.put(index, INITIAL_STORE_STONES_COUNT);
                } else {
                    pits.put(index, playerInitialPitStonesCount);
                }
            }
            setGameStatus(GameStatus.INITIALIZED);
        }
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setGameResult(GameResult gameResult) {
        this.gameResult = gameResult;
    }

}
