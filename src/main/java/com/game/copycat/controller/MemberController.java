package com.game.copycat.controller;

import com.game.copycat.domain.JoinRequest;
import com.game.copycat.domain.LoginRequest;
import com.game.copycat.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public String join(
            @Valid @ModelAttribute JoinRequest joinRequest,
            BindingResult bindingResult
    ) {
        boolean isSuccess = memberService.join(joinRequest, bindingResult);
        // 성공하면 로그인 화면으로 리다이렉트
        if (isSuccess) {
            return "redirect:/login";
        }
        // 실패하면 로그인 화면을 띄워줌 (입력했던 정보 그대로 유지하기 위해)
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute LoginRequest loginRequest,
            BindingResult bindingResult
            ) {
        boolean isSuccess = memberService.login(loginRequest, bindingResult);
        if (isSuccess) {
            return "redirect:/rooms";
        }
        return "login";
    }
}
