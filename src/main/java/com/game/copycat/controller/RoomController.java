package com.game.copycat.controller;

import com.game.copycat.domain.Room;
import com.game.copycat.dto.RoomRequest;
import com.game.copycat.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    @PostMapping
    public String createRoom(
            @ModelAttribute @Valid RoomRequest request,
            Model model
            ) {
        // 방을 생성하고 방으로 이동
        Room room = roomService.createRoom(request);
        model.addAttribute("room", room);
        return String.format("/rooms/%s", room.getId());
    }
}
