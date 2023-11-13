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
    private String roomName;
    private TurnState currentState; // 현재 턴의 공격, 수비 상태
    private Integer currentNum; // 현재 인원 수
    private List<String> queue;
    @Builder
    public Game(String id, String roomName) {
        this.id = id;
        this.roomName = roomName;
        this.currentState = TurnState.OFFENCE; // 공격으로 시작
        this.currentNum = 1; // 인원 수는 한 명
        this.queue = new LinkedList<>(); // 게임이 시작할 때 큐를 채운다.
    }
}
