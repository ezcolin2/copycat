package com.game.copycat.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class JoinRequest {
    @NotBlank
    private String memberId; // 로그인 할 때 사용
    @NotBlank
    private String nickname;
    @NotBlank
    private String password;

//    @Builder
//    public MemberRequest(String memberId, String nickname, String password) {
//        this.memberId = memberId;
//        this.nickname = nickname;
//        this.password = password;
//    }
}
