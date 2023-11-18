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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate template;

    // 연결
    @MessageMapping("/connect/{id}")
    @SendTo("/topic/connection/{id}")
    public ConnectionMessage connection(
            @DestinationVariable("id") String id,
            Authentication authentication
    ) {
        System.out.println("name = " + authentication.getName());
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();
        // 게임 정보
        Optional<Game> findGame = gameService.enterGame(id, member.getMemberId());
        // 접속할 수 없을 때
        if (findGame.isEmpty()) {
            return ConnectionMessage.builder()
                    .message("접속할 수 없습니다.")
                    .isSuccess(false).build();
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

    // 연결 종료
    @MessageMapping("/disconnect/{id}")
    @SendTo("/topic/connection/{id}")
    public ConnectionMessage disconnection(
            @DestinationVariable("id") String id,
            Authentication authentication
            ) {
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        // 방 정보
        gameService.leaveGame(id, member.getMemberId());
        MemberInfo info = MemberInfo.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .total(member.getTotal())
                .win(member.getWin())
                .lose(member.getLose()).build();
        return ConnectionMessage.builder()
                .connection(Connection.DISCONNECT)
                .isSuccess(true)
                .message(member.getNickname() + "님께서 퇴장하셨습니다")
                .result(info).build();
    }

    // 방장 게임 시작
    @MessageMapping("/start/{id}")
    @SendTo("/topic/game/{id}")
    public ConnectionMessage start(
            @DestinationVariable("id") String id,
            Authentication authentication
    ) {
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        // 방 정보
        boolean isSuccess = gameService.startGame(id, member.getMemberId());
        // 성공했다면
        return null;

    }
}
