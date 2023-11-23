package com.game.copycat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Member implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memberId; // 로그인 할 때 사용
    private String nickname;
    private String password;
    private Integer total;
    private Integer win;
    private Integer lose;

    public void win() {
        this.win+=1;
        this.total+=1;
    }
    public void lose() {
        this.win-=1;
        this.total-=1;
    }
    @Builder
    public Member(
            String memberId,
            String nickname,
            String password
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.password = password;
        this.total = 0;
        this.win = 0;
        this.lose = 0;

    }

}
