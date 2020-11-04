package org.task.kalah.service;

import org.task.kalah.dto.Game;
import org.task.kalah.dto.GameStatus;
import org.task.kalah.exception.GameStatusException;
import org.task.kalah.exception.MissingGameException;
import org.task.kalah.repository.GameRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {

    @Value("${INITIAL_STONES_COUNT}")
    private int playerInitialPitStonesCount;
    @Value("${INITIAL_PITS_COUNT}")
    private int playerPitsCount;

    private final GameRepository gameRepository;
    private final GameIdGenerator gameIdGenerator;
    private final GameRulesService gameRulesService;

    public GameService(GameRepository gameRepository, GameIdGenerator gameIdGenerator, GameRulesService gameRulesService) {
        this.gameRepository = gameRepository;
        this.gameIdGenerator = gameIdGenerator;
        this.gameRulesService = gameRulesService;
    }

    /**
     * Creates new game and initializes it.
     *
     * @return game id.
     */
    public Long createGame() {
        Long gameId = gameIdGenerator.getNextId();
        //TODO could be initializes in a background
        Game game = new Game(gameId, playerInitialPitStonesCount, playerPitsCount);
        game.initialize();
        gameRepository.save(game);
        //TODO end background task
        return gameId;
    }

    /**
     * Returns games
     *
     * @param gameId gameId to be found.
     * @return game state.
     */
    public Optional<Game> getGame(Long gameId) {
        return gameRepository.retrieve(gameId);
    }

    /**
     * Sow stones.
     *
     * @param gameId game to be found.
     * @param pitId  we use to sow from.
     * @return game state.
     */
    public Game sowStones(Long gameId, Integer pitId) {
        Optional<Game> gameOptional = gameRepository.retrieve(gameId);
        if (gameOptional.isEmpty()) {
            throw new MissingGameException("Game with such id does not exist");
        }

        Game game = gameOptional.get();
        //check if game is playable
        GameStatus gameStatus = game.getGameStatus();
        if (gameStatus == GameStatus.ENDED) {
            throw new GameStatusException("Game has ended already");
        }
        if (!gameStatus.isPlayable()) {
            throw new GameStatusException("Game is not playable yet");
        }
        gameRulesService.sowStones(pitId, game);
        return game;
    }
}
