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
    private List<String> queue;
    private Long creatorId; // 방장 아이디 게임 시작할 때 사용
    @Builder
    public Game(String id, String roomName, Long creatorId) {
        this.id = id;
        this.roomName = roomName;
        this.currentState = TurnState.OFFENCE; // 공격으로 시작
        this.queue = new LinkedList<>(); // 게임이 시작할 때 큐를 채운다.
        this.creatorId = creatorId;
    }

}
