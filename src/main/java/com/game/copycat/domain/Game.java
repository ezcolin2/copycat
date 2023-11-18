package com.game.copycat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RedisHash("game")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Game{
    @Id
    private String id; // Room id와 동일
    private TurnState currentState; // 현재 턴의 공격, 수비 상태
    private LinkedList<String> queue; // memberId 저장
    @Setter
    private String creatorId; // 방장 아이디
    @Setter
    private String participantId; // 방장이 아닌 아이디
    private Integer currentNum;
    private Integer currentRound; // 현재 라운드

    @Builder
    public Game(String id, String creatorId) {
        this.id = id;
        this.currentState = TurnState.OFFENCE; // 공격으로 시작
        this.queue = new LinkedList<>(); // 게임이 시작할 때 큐를 채운다.
        this.queue.add("hello");
        this.creatorId = creatorId;
        this.participantId = "";
        this.currentNum = 1;
        this.currentRound = 1;
    }

    public void enterGame() {
        this.currentNum+=1;
    }
    public void leaveGame() {
        this.currentNum-=1;
    }
}
