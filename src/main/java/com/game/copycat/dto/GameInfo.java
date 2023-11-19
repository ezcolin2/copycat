package com.game.copycat.dto;

import com.game.copycat.domain.GameStatus;
import com.game.copycat.domain.TurnState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameInfo {
    private TurnState turnState;
    private String memberId;
    private Integer currentRound;
    @Builder
    public GameInfo(
            TurnState turnStatus,
            String memberId,
            Integer currentRound
    ) {
        this.turnState = turnStatus;
        this.memberId = memberId;
        this.currentRound = currentRound;
    }
}
