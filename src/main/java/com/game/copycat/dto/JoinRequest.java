package com.game.copycat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
public class JoinRequest {
    // 가장 처음 로그인 화면에 들어가면 타임리프에 바인딩이 되어야 해서 비어있는 JoinRequest가 필요함
    // 그래서 @NotBlank가 아닌 @NoNull을 넣고 처음에는 비어있는 JoinRequest를 생성해서 넣어줌
    @NotNull
    private String memberId; // 로그인 할 때 사용
    @NotNull
    private String nickname;
    @NotNull
    private String password;
}
