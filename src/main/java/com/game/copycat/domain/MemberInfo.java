package com.game.copycat.domain;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberInfo {
    private String memberId;
    private String nickname;
    private int total;
    private int win;
    private int lose;
    @Builder
    public MemberInfo(
            String memberId,
            String nickname,
            int total,
            int win,
            int lose
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.total = total;
        this.win = win;
        this.lose = lose;
    }
}
