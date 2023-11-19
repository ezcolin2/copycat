package com.game.copycat.dto;

import com.game.copycat.domain.TurnState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomInfo {
    private TurnState turnState; // 공격이면 이미지 정보, 수비면 점수 정보가 담김
    private Integer currentRound;
    private String image; // 수비면 null, 공격이면 값 존재
    private String memberId;
    private String creatorId;
    private String participantId;
    private Integer creatorScore;
    private Integer participantScore;
    @Builder
    public RoomInfo(
            TurnState turnStatus,
            Integer currentRound,
            String creatorId,
            String participantId,
            Integer creatorScore,
            Integer participantScore,
            String image,
            String memberId
    ) {
        this.turnState = turnStatus;
        this.currentRound = currentRound;
        this.creatorId = creatorId;
        this.participantId = participantId;
        this.creatorScore = creatorScore;
        this.participantScore = participantScore;
        this.image = image;
        this.memberId = memberId;
    }
}
