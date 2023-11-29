package com.game.copycat.repository;

import com.game.copycat.domain.Game;
import com.game.copycat.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<Game, String> {
    Optional<Game> findByCreatorId(String creatorId);
    Optional<Game> findByParticipantId(String creatorId);
}
