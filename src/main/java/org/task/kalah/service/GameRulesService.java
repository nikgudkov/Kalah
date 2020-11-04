package org.task.kalah.service;

import org.task.kalah.dto.Game;
import org.task.kalah.dto.GameStatus;
import org.task.kalah.dto.Player;
import org.task.kalah.exception.EmptyPitException;
import org.task.kalah.exception.WrongPitException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.IntStream;

@Service
public class GameRulesService {

    /**
     * Sows stones across the board.
     *
     * @param pitId to retain the stones.
     * @param game  game.
     */
    public void sowStones(int pitId, Game game) {

        // ----- Validation start -----

        //check if pit exists
        int pitsSize = game.getPits().size();
        if (pitId < 1 || pitId > pitsSize) {
            throw new WrongPitException("Pit does not exist");
        }

        //check if we try to sow from the Store
        int playerPitsCount = game.getPlayerPitsCount();
        if (pitId % (playerPitsCount + 1) == 0) {
            throw new WrongPitException("Can't sow from the store");
        }

        //check if we try to get stones from the opponent player's pit
        Player player = game.getCurrentPlayer();
        boolean playerOneTurn = player == Player.FIRST;
        int currentPlayerStoreIndex = getCurrentPlayerStoreIndex(game, player);

        if (playerOneTurn && pitId > playerPitsCount
                || !playerOneTurn && pitId <= playerPitsCount) {
            throw new WrongPitException("Can't sow from opponent's pit");
        }

        Map<Integer, Integer> pits = game.getPits();
        //retain all stones from current pit
        Integer stonesCount = pits.replace(pitId, 0);

        //check if we try to get stones from empty pit
        if (stonesCount == 0) {
            throw new EmptyPitException("Can't sow from an empty pit");
        }

        // ----- Validation end -----

        int currentPitId = pitId;
        //Put one stone in every pit
        for (int i = 0; i < stonesCount; i++) {
            currentPitId++;
            //check if we need to start from the first pit again
            if (currentPitId > pits.size()) {
                currentPitId = 1;
            }
            addStonesToPitIndex(currentPitId, pits, 1);
        }

        //check if last pit was a Store pit of a current player
        //doesn't change player's turn
        if (currentPlayerStoreIndex == currentPitId) {
            //check if we can end the game
            if (isGameEnded(game)) {
                endTheGame(game);
            }
            return;
        }

        //if we end up in opposite player's pit we end current player's turn
        if (playerOneTurn && (currentPlayerStoreIndex * 2) == currentPitId
                || !playerOneTurn && (currentPlayerStoreIndex / 2) == currentPitId) {
            game.setCurrentPlayer(playerOneTurn ? Player.SECOND : Player.FIRST);
            return;
        }

        //if we end up in our pit we check if it has only one stone and get the opposite player's pit stones
        //doesn't change player's turn
        if (pits.get(currentPitId) == 1) {
            int oppositePitId = pits.size() - currentPitId;

            //retain all stones from opposite pit
            Integer oppositePitStonesCount = pits.replace(oppositePitId, 0);
            //retain all stones from current pit
            Integer currentPitStonesCounts = pits.replace(currentPitId, 0);

            addStonesToPitIndex(currentPlayerStoreIndex, pits, oppositePitStonesCount + currentPitStonesCounts);
            return;
        }


        //check if we can end the game
        if (isGameEnded(game)) {
            endTheGame(game);
            return;
        }

        game.setCurrentPlayer(playerOneTurn ? Player.SECOND : Player.FIRST);
    }

    /**
     * Get current index of a player's Store.
     *
     * @param game   current game.
     * @param player current player.
     * @return current player store index.
     */
    private int getCurrentPlayerStoreIndex(Game game, Player player) {
        int currentPlayerStoreIndex;
        if (player == Player.FIRST) {
            currentPlayerStoreIndex = game.getPlayerPitsCount() + 1;
        } else {
            currentPlayerStoreIndex = (game.getPlayerPitsCount() + 1) * 2;
        }
        return currentPlayerStoreIndex;
    }

    /**
     * Checks if current player's pits are empty
     *
     * @param game game played.
     * @return true if game is ended, false otherwise.
     */
    private boolean isGameEnded(Game game) {
        Player player = game.getCurrentPlayer();

        int rangeStart;
        int rangeEnd;
        if (player == Player.FIRST) {
            rangeStart = 1;
            rangeEnd = game.getPlayerPitsCount();
        } else {
            rangeStart = game.getPlayerPitsCount() + 2;
            rangeEnd = game.getPits().size();
        }

        Map<Integer, Integer> pits = game.getPits();
        return IntStream.rangeClosed(rangeStart, rangeEnd).allMatch(pitId -> pits.get(pitId) == 0);
    }

    private void endTheGame(Game game) {
        game.setGameStatus(GameStatus.ENDED);
        //TODO calculate results in a background
        return;
    }

    /**
     * Adds stones to pit index.
     *
     * @param pitId       pitId.
     * @param pits        all pits.
     * @param stonesCount number of stone to add to the chosen pit.
     */
    private void addStonesToPitIndex(int pitId, Map<Integer, Integer> pits, int stonesCount) {
        Integer pitStonesCount = pits.get(pitId);
        pits.put(pitId, pitStonesCount + stonesCount);
    }

}
