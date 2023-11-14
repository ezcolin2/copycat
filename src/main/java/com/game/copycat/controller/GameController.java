package com.game.copycat.controller;

import com.game.copycat.domain.Game;
import com.game.copycat.domain.Member;
import com.game.copycat.domain.PrincipalDetails;
import com.game.copycat.domain.Room;
import com.game.copycat.dto.Connection;
import com.game.copycat.dto.ConnectionMessage;
import com.game.copycat.dto.MemberInfo;
import com.game.copycat.service.GameService;
import com.game.copycat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final RoomService roomService;

    @MessageMapping("/connect/{id}")
    @SendTo("/topic/connection/{id}")
    public ConnectionMessage connection(
            @DestinationVariable("id") String id,
            Authentication authentication
    ) {
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();
        // 방 정보
        Optional<Room> findRoom = roomService.findById(id);
        if (findRoom.isEmpty()) {
            return ConnectionMessage.builder()
                    .message("해당 아이디를 가진 게임이 없습니다.")
                    .isSuccess(false).build();
        }
        Room room = findRoom.get();
        Integer currentNum = room.getCurrentNum();
        // 방이 이미 차있다면 거부
        // 그런데 이거는 이미 Room에 입장할 때 체크하기 때문에 실질적으로 아래 로직이 실행될 일은 없어야 함
        if (currentNum >= 2) {
            return ConnectionMessage.builder()
                    .message("더이상 입장이 불가능합니다.")
                    .isSuccess(false).build();
        }
        // 자신이 방을 생성한 사람이라면
        else if (currentNum == 0) {
            gameService.createGame(room.getId(), member.getId(), room.getRoomName());
            // 방 인원 수 증가
            roomService.enterRoom(room.getId());

        }
        // 이미 생성된 방에 들어온 사람이라면
        else if (currentNum == 1) {
            // 방 인원 수 증가
            roomService.enterRoom(room.getId());
        }
        MemberInfo info = MemberInfo.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .total(member.getTotal())
                .win(member.getWin())
                .lose(member.getLose()).build();
        return ConnectionMessage.builder()
                .connection(Connection.CONNECT)
                .isSuccess(true)
                .message(member.getNickname() + "님께서 입장하셨습니다")
                .result(info).build();
    }

    @MessageMapping("/disconnect/{id}")
    @SendTo("/topic/connection/{id}")
    public ConnectionMessage disconnection(
            @PathVariable("id") String id,
            Authentication authentication
            ) {
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        // 방 정보
        Optional<Room> findRoom = roomService.findById(id);
        if (findRoom.isEmpty()) {
            return ConnectionMessage.builder()
                    .message("해당 아이디를 가진 게임이 없습니다.")
                    .isSuccess(false).build();
        }
        roomService.leaveRoom(findRoom.get().getId());
        MemberInfo info = MemberInfo.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .total(member.getTotal())
                .win(member.getWin())
                .lose(member.getLose()).build();
        return ConnectionMessage.builder()
                .connection(Connection.CONNECT)
                .isSuccess(true)
                .message(member.getNickname() + "님께서 퇴장하셨습니다")
                .result(info).build();
    }
}
