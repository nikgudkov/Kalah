package org.task.kalah.repository;

import org.task.kalah.dto.Game;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class GameRepository {

    private final Map<Long, Game> games = new HashMap<>();

    public Optional<Game> retrieve(Long id) {
        Game game = games.get(id);
        return Optional.ofNullable(game);
    }

    public void save(Game game) {
        games.put(game.getId(), game);
    }

}
