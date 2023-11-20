package com.game.copycat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse {
    private Integer code;
    private String message;

    @Builder
    public ResultResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
