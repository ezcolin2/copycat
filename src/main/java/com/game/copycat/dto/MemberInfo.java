package com.game.copycat.dto;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberInfo {
    private Long id;
    private String memberId;
    private String nickname;
    private int total;
    private int win;
    private int lose;
    @Builder
    public MemberInfo(
            Long id,
            String memberId,
            String nickname,
            int total,
            int win,
            int lose
    ) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
        this.total = total;
        this.win = win;
        this.lose = lose;
    }
}
