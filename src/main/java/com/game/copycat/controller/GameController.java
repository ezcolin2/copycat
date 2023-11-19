package com.game.copycat.controller;

import com.game.copycat.domain.*;
import com.game.copycat.dto.*;
import com.game.copycat.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.LinkedList;
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
    public GameInfo start(
            @DestinationVariable("id") String id,
            Authentication authentication
    ) {
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        // 방 정보
        boolean isSuccess = gameService.startGame(id, member.getMemberId());
        // 실패한다면 (방장이 아닌 사람이 시작, 사람이 없는데 시작)
        if (!isSuccess) {
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(member.getMemberId())
                    .message("게임을 시작할 수 없습니다.").build();
            template.convertAndSend("/topic/message/" + id, socketMessage);
            return null;
        }
        // 성공한다면
        else {
            // 게임 정보 가져오기
            Game game = gameService.findById(id).get();
            // 우선 현재 턴을 진행할 유저에게 너의 차례라고 알려주고 공격, 수비 정보를 줌
            // 유저는 정보를 받아서 3초 뒤 자신의 역할에 맞는 정보를 다시 예정
            HashMap<String, String> map = new HashMap<>();
            map.put("memberId", game.getCreatorId());
            map.put("state", game.getCurrentState().toString());
            template.convertAndSendToUser(game.getCreatorId(), "/topic/game/" + id, map);
            // 모두가 현재 누구 차례인지 알 수 있도록 메시지 전송
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(game.getCreatorId())
                    .message(member.getNickname() + "님이 공격할 차례입니다.").build();
            template.convertAndSend("/topic/message/" + id, socketMessage);
            // 게임 정보 모두에게 전송
            GameInfo gameInfo = GameInfo.builder()
                    .memberId(game.getCreatorId())
                    .turnStatus(game.getCurrentState())
                    .currentRound(game.getCurrentRound()).build();
            return gameInfo; //
        }
    }
    @MessageMapping("/offense/{id}")
    @SendTo("/topic/game/{id}")
    public RoomInfo offense(
            @DestinationVariable("id") String id,
            Authentication authentication,
            String image
    ) {
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();
        // 현재 누구 차례인지 확인하고 게임 정보 변경
        GameInfo gameInfo = gameService.changeTurn(id);
        System.out.println("gameInfo = " + gameInfo.getMemberId());
        if (!member.getMemberId().equals(gameInfo.getMemberId())) {
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(member.getMemberId())
                    .message(member.getNickname()+"님의 차례가 아닙니다.").build();
            template.convertAndSend("/topic/message/" + id, socketMessage);
            return null;
        }
        // 현재 차례가 수비인데 공격이 들어왔다면 거절
        if (gameInfo.getTurnState().equals(TurnState.DEFENSE)) {
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(member.getMemberId())
                    .message(gameInfo.getTurnState().toString()+"의 차례입니다.").build();
            template.convertAndSend("/topic/message/" + id, socketMessage);
            return null;
        }
        // 바뀐 게임 정보 가져오기
        Game game = gameService.findById(id).get();
        LinkedList<String> queue = game.getQueue();
        String nextMemberId = queue.getFirst();

        // 우선 현재 턴을 진행할 유저에게 너의 차례라고 알려주고 공격, 수비 정보를 줌
        // 유저는 정보를 받아서 3초 뒤 자신의 역할에 맞는 정보를 다시 보낼 예정
        HashMap<String, String> map = new HashMap<>();
        map.put("memberId", nextMemberId);
        map.put("state", game.getCurrentState().toString());
        template.convertAndSendToUser(nextMemberId, "/topic/game/" + id, map);
        // 모두가 현재 누구 차례인지 알 수 있도록 메시지 전송
        SocketMessage socketMessage = SocketMessage.builder()
                .memberId(nextMemberId)
                .message(nextMemberId + "님이 수비할 차례입니다.").build();
        template.convertAndSend("/topic/message/" + id, socketMessage);
        // 모두에게 바뀐 게임 정보 보내기
        RoomInfo roomInfo = RoomInfo.builder()
                .memberId(nextMemberId)
                .turnStatus(game.getCurrentState())
                .currentRound(game.getCurrentRound())
                .image(image)
                .build();
        return roomInfo;
    }
    @MessageMapping("/defense/{id}")
    @SendTo("/topic/game/{id}")
    public RoomInfo defense(
            @DestinationVariable("id") String id,
            Authentication authentication,
            Integer score
    ) {
        // 유저 정보
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();
        // 현재 누구 차례인지 확인하고 게임 정보 변경와 점수 추가
        GameInfo gameInfo = gameService.changeAndAddScore(id, member.getMemberId(), score);
        if (!member.getMemberId().equals(gameInfo.getMemberId())) {
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(member.getMemberId())
                    .message(member.getNickname()+"님의 차례가 아닙니다.").build();
            template.convertAndSend("/topic/message/" + id, socketMessage);
            return null;
        }
        // 현재 차례가 공격인데 수비가 들어왔다면 거절
        if (gameInfo.getTurnState().equals(TurnState.OFFENSE)) {
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(member.getMemberId())
                    .message(gameInfo.getTurnState().toString()+"의 차례입니다.").build();
            template.convertAndSend("/topic/message/" + id, socketMessage);
            return null;
        }
        // 바뀐 게임 정보 가져오기
        Game game = gameService.findById(id).get();
        LinkedList<String> queue = game.getQueue();
        String nextMemberId = queue.getFirst();
        // 우선 현재 턴을 진행할 유저에게 너의 차례라고 알려주고 공격, 수비 정보를 줌
        // 유저는 정보를 받아서 3초 뒤 자신의 역할에 맞는 정보를 다시 보낼 예정
        HashMap<String, String> map = new HashMap<>();
        map.put("memberId", nextMemberId);
        map.put("state", game.getCurrentState().toString());
        template.convertAndSendToUser(nextMemberId, "/topic/game/" + id, map);
        // 모두가 현재 누구 차례인지 알 수 있도록 메시지 전송
        SocketMessage socketMessage = SocketMessage.builder()
                .memberId(nextMemberId)
                .message(nextMemberId + "님이 공격할 차례입니다.").build();
        template.convertAndSend("/topic/message/" + id, socketMessage);
        // 모두에게 바뀐 게임 정보 보내기
        RoomInfo roomInfo = RoomInfo.builder()
                .memberId(nextMemberId)
                .turnStatus(game.getCurrentState())
                .currentRound(game.getCurrentRound())
                .creatorId(game.getCreatorId())
                .participantId(game.getParticipantId())
                .creatorScore(game.getCreatorScore())
                .participantScore(game.getParticipantScore())
                .build();
        return roomInfo;
    }

//    @MessageMapping("/game/{id}")
//    @SendTo("/topic/game/{id}")
//    public GameInfo game(
//            @DestinationVariable("id") String id,
//            Authentication authentication
//    ) {
//
//    }
}
