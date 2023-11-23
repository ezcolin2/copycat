package com.game.copycat.controller;

import com.game.copycat.domain.Game;
import com.game.copycat.domain.Member;
import com.game.copycat.domain.PrincipalDetails;
import com.game.copycat.domain.Room;
import com.game.copycat.dto.JoinRequest;
import com.game.copycat.dto.MemberInfo;
import com.game.copycat.service.GameService;
import com.game.copycat.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private final RoomService roomService;
    private final GameService gameService;
    @GetMapping("/login")
    public String login(Model model) {
        JoinRequest memberRequest = new JoinRequest("", "", "");
        // 타임리프에서 오류를 방지하기 위해서 비어있는 BindingResult와 JoinRequest를 넣어줌.
        model.addAttribute("bindingResult", new BeanPropertyBindingResult(new Object(), "bindingResult"));
        model.addAttribute("memberRequest", memberRequest);
        return "login";
    }

    @GetMapping("/rooms")
    public String rooms(Model model, Authentication authentication) {
        // 로그인 한 상태라면 authentication을 가져와서 유저 정보를 Model에 넣어준다.
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Member member = principal.getMember();
        MemberInfo info = MemberInfo.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .total(member.getTotal())
                .win(member.getWin())
                .lose(member.getLose()).build();
        model.addAttribute("info", info);

        // 게임 방 리스트도 가져와서 Model에 넣어준다.
        List<Room> rooms = roomService.findAll();
        model.addAttribute("rooms", rooms);
        return "rooms";
    }

    @GetMapping("/rooms/{id}")
    public String games(@PathVariable("id") String id, Model model) {
        Room room = roomService.findById(id);
        model.addAttribute("room", room);
        return "game";
    }
}
