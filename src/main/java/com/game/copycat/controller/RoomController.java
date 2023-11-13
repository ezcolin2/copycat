package com.game.copycat.controller;

import com.game.copycat.domain.Game;
import com.game.copycat.domain.Room;
import com.game.copycat.dto.RoomRequest;
import com.game.copycat.service.GameService;
import com.game.copycat.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final GameService gameService;
    @PostMapping
    public String createRoom(
            @ModelAttribute @Valid RoomRequest request,
            Model model
            ) {
        // 방을 생성하고 방으로 이동
        Room room = roomService.createRoom(request);
        Game game = gameService.createGame(room.getId(), room.getRoomName());
        model.addAttribute("room", room);
        model.addAttribute("game", room);
        return String.format("redirect:/rooms/%s", room.getId());
    }
//    @GetMapping("/{id}")
//    public String enterRoom(@PathVariable("id") String id, String password, Model model) {
//        Optional<Room> find = roomService.findById(id);
//        if (find.isEmpty()) {
//            // 찾지 못 하면 room으로
//            return "rooms";
//        }
//        Room room = find.get();
//        // 비밀번호 같다면 이동
//        if (room.getPassword().equals(password)) {
//            model.addAttribute("room", room);
//        }
//        // 틀리다면 비밀번호 입력 창으로
//        else{
//            return "password";
//        }
//
//    }
}
