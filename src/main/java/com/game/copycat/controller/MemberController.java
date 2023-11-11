package com.game.copycat.controller;

import com.game.copycat.domain.Room;
import com.game.copycat.dto.JoinRequest;
import com.game.copycat.dto.LoginRequest;
import com.game.copycat.dto.MemberInfo;
import com.game.copycat.service.MemberService;
import com.game.copycat.service.RoomService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final RoomService roomService;

    @PostMapping("/join")
    public String join(
            @Valid @ModelAttribute("memberRequest") JoinRequest joinRequest,
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
            @Valid @ModelAttribute("memberRequest") LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpSession httpSession,
            Model model
            ) {
        Optional<MemberInfo> memberInfo = memberService.login(loginRequest, bindingResult);
        // 로그인에 성공했다면 세션에 정보 저장
        if (memberInfo.isPresent()) {
            httpSession.setAttribute("member", memberInfo.get());
            model.addAttribute("info", memberInfo.get());
            // 현재 존재하는 방 정보도 담아서
            List<Room> rooms = roomService.findAll();
            model.addAttribute("rooms", rooms);
            return "rooms";
        }
        return "login";
    }
}
