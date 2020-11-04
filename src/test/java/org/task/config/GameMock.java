package org.task.config;

import org.task.kalah.dto.Game;
import org.task.kalah.dto.Player;

import java.util.Map;

public class GameMock extends Game {

    public GameMock(Long id, int playerInitialPitStonesCount, int playerPitsCount) {
        super(id, playerInitialPitStonesCount, playerPitsCount);
    }

    public void setPits(Map<Integer, Integer> pits) {
        this.pits = pits;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

}
