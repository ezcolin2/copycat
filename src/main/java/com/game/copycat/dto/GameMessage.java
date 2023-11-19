package com.game.copycat.dto;

import com.game.copycat.domain.GameStatus;
import com.game.copycat.domain.TurnState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMessage {
    private GameStatus gameStatus; // 렌더링 또는 요청
    private GameInfo gameInfo;
    private Object result;

    @Builder
    public GameMessage(
            GameStatus gameStatus,
            GameInfo gameInfo,
            Object result
    ) {
        this.gameStatus = gameStatus;
        this.gameInfo = gameInfo;
    }
}
