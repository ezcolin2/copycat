package com.game.copycat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocketMessage {
    private String memberId;
    private String message;

    @Builder
    public SocketMessage(String memberId, String message) {
        this.memberId = memberId;
        this.message = message;
    }
}
