package com.game.copycat.service;

import com.game.copycat.domain.Game;
import com.game.copycat.domain.Member;
import com.game.copycat.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    public Game createGame(String roomId, Long creatorId, String roomName) {
        Game game = Game.builder()
                .id(roomId)
                .roomName(roomName)
                .creatorId(creatorId)
                .build();
        Game save = gameRepository.save(game);
        return save;
    }

    public Optional<Game> findById(String id) {
        return gameRepository.findById(id);
    }
}
