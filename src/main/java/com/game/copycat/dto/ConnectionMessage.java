package com.game.copycat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionMessage {
    Connection connection;
    private boolean isSuccess; // 성공 여부
    private String message; // 메시지
    private Object result;

    @Builder
    public ConnectionMessage(Connection connection, boolean isSuccess, String message, Object result) {
        this.connection = connection;
        this.isSuccess = isSuccess;
        this.message = message;
        this.result = result;
    }

}
