package com.game.copycat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class RoomRequest {
    @NotBlank
    private String roomName;
    private String password; // 비밀번호가 없다면 빈 문자열
    private String isLocked;
    @NotNull
    private Integer round;
}
