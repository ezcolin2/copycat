package com.game.copycat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String memberId; // 로그인 할 때 사용
    @NotBlank
    private String password;

}
