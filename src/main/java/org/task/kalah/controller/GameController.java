package org.task.kalah.controller;

import org.task.kalah.dto.Game;
import org.task.kalah.dto.response.GameCreatedResponse;
import org.task.kalah.dto.response.GameSowStonesResponse;
import org.task.kalah.service.GameService;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PutMapping(value = "/{gameId}/pits/{pitId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public GameSowStonesResponse sowStones(@NonNull @PathVariable("gameId") Long gameId, @NonNull @PathVariable("pitId") Integer pitId) {
        Game game = gameService.sowStones(gameId, pitId);
        return new GameSowStonesResponse(gameId, createGameUri(gameId), game.getPits());
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public GameCreatedResponse createGame() {
        Long gameId = gameService.createGame();
        String gameUri = createGameUri(gameId);
        return new GameCreatedResponse(gameId, gameUri);
    }

    @GetMapping(value = "/{gameId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Optional<Game> getGame(@NonNull @PathVariable("gameId") Long gameId) {
        return gameService.getGame(gameId);
    }

    private String createGameUri(Long gameId) {
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return String.format("%s/%s/%d", baseUrl, "games", gameId);
    }

    //Will suffice for this task
    @ExceptionHandler
    public String exceptionHandler(Exception e) {
        return e.getMessage();
    }

}
