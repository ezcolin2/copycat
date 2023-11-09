package com.game.copycat.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class JoinRequest {
    @NotBlank
    private String memberId; // 로그인 할 때 사용
    @NotBlank
    private String nickname;
    @NotBlank
    private String password;
}
