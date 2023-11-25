package com.game.copycat.service;

import com.game.copycat.domain.Game;
import com.game.copycat.domain.Member;
import com.game.copycat.domain.Room;
import com.game.copycat.domain.TurnState;
import com.game.copycat.dto.GameInfo;
import com.game.copycat.dto.SocketMessage;
import com.game.copycat.exception.MemberNotFoundException;
import com.game.copycat.exception.RoomAndGameNotFoundException;
import com.game.copycat.repository.GameRepository;
import com.game.copycat.repository.MemberRepository;
import com.game.copycat.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate template;
    @Value("${image.path}")
    private String path; // 이미 저장 경로
    private void deleteImage(String roomId) {
        String imagePath = path + roomId + ".png";

        try {
            File file = new File(imagePath);

            // 이미지 파일이 존재하는지 확인 후 삭제
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Game createGame(String gameId, String creatorId) {
        Game game = Game.builder()
                .id(gameId)
                .creatorId(creatorId)
                .build();
        Game save = gameRepository.save(game);
        return save;
    }

//    public Game initialize(TurnState turnState, LinkedList<String> queue) {
//        queue
//    }

    public Game findById(String id) {
        Optional<Game> findGame = gameRepository.findById(id);
        if (findGame.isEmpty()){
            throw new RoomAndGameNotFoundException("gameId", id);
        }
        return findGame.get();
    }

    public Optional<Game> enterGame(String gameId, String memberId) {
        Optional<Game> findGame = gameRepository.findById(gameId);
        // 자신이 방을 만든 사람이면
        if (findGame.isEmpty()) {
            // 방 생성
            Game game = Game.builder()
                    .id(gameId)
                    .creatorId(memberId)
                    .build();
            LinkedList<String> queue = game.getQueue();
            queue.add("");
            Game save = gameRepository.save(game);
            return Optional.of(save);
        } else if (findGame.get().getCurrentNum() == 1) {
            // 자신이 참가자라면 참가자 이름 변경 후 인원 수 증가
            Game game = findGame.get();
            game.setParticipantId(memberId);
            game.enterGame();
            return Optional.of(gameRepository.save(game));
        } else if (findGame.get().getCurrentNum() >= 2) {
            // 꽉 찼다면 거절
            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    public void leaveGame(String gameId, String memberId) {
        Optional<Game> findGame = gameRepository.findById(gameId);
        Game game = findGame.get();
        String creatorId = game.getCreatorId();
        String participantId = game.getParticipantId();
        // 한 명 남은 사람이 떠난다면 삭제
        if (game.getCurrentNum() == 1) {
            gameRepository.delete(game);
            roomRepository.delete(roomRepository.findById(gameId).get());
            deleteImage(gameId);

        }
        // 아직 사람이 남아있고 방 생성자가 떠난다면 참가자를 방 생성자로 변경
        else if (memberId.equals(creatorId)) {
            game.setCreatorId(game.getParticipantId());
            game.setParticipantId("");
            game.leaveGame();
            gameRepository.save(game);
        }
        // 아직 사람이 남아있고 방 생성자가 떠난다면 참가자를 비움
        else if (memberId.equals(participantId)) {
            game.setParticipantId("");
            game.leaveGame();
            gameRepository.save(game);
        }
        // 참가자 수 줄이고 참가자 아이디 공백으로
    }

    public boolean startGame(String gameId, String memberId) {
        Optional<Game> findGame = gameRepository.findById(gameId);
        Game game = findGame.get();
        // 만약 참가자가 시작하거나 사람이 없다면 거절
        if (memberId.equals(game.getParticipantId())) {
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(memberId)
                    .message("방장만 게임을 시작할 수 있습니다.").build();
            template.convertAndSend("/topic/message/" + gameId, socketMessage);
            return false;
        } else if (game.getCurrentNum()!=2){
            SocketMessage socketMessage = SocketMessage.builder()
                    .memberId(memberId)
                    .message("인원이 충분하지 않습니다.").build();
            template.convertAndSend("/topic/message/" + gameId, socketMessage);
            return false;

        }
        // 방장만 시작 가능
        LinkedList<String> queue = game.getQueue();
        String creatorId = game.getCreatorId();
        String participantId = game.getParticipantId();
        // round 수만큼 큐에 순서 추가
        Optional<Room> findRoom = roomRepository.findById(gameId);
        Room room = findRoom.get();
        if (queue.size()==1) {
            String pop = queue.pop();
            System.out.println("pop = " + pop);
        }
        System.out.println("i = " + room.getRound());
        for (int i = 0; i < room.getRound(); i++) {
            queue.add(creatorId);
            queue.add(participantId);
            queue.add(participantId);
            queue.add(creatorId);
            gameRepository.save(game);
        }
        return true;
    }

    // 턴을 변경하는 함수
    public GameInfo changeTurn(String gameId) {
        Game game = gameRepository.findById(gameId).get();
        // 현재 차례인 id 가져옴
        LinkedList<String> queue = game.getQueue();
        String memberId = queue.pop();
        // 현재 차례 공/수 정보 가져오고 변경
        TurnState currentState = game.getCurrentState();
        game.changeStatus();
        // 현재 라운드 정보 가져오고 변경
        Integer currentRound = game.getCurrentRound();
        game.nextRound();
        gameRepository.save(game);
        GameInfo gameInfo = GameInfo.builder()
                .turnStatus(currentState)
                .currentRound(currentRound)
                .memberId(memberId).build();
        return gameInfo;
    }
    // 점수를 추가하는 함수
    public GameInfo changeAndAddScore(String gameId, String currentMemberId, Integer score) {
        Game game = gameRepository.findById(gameId).get();
        // 방장의 수비 차례라면
        if (currentMemberId.equals(game.getCreatorId())) {
            game.addCreatorScore(score);
        }
        else{
            game.addParticipantScore(score);
        }
        // 현재 차례인 id 가져옴
        LinkedList<String> queue = game.getQueue();
        String memberId = queue.pop();
        // 현재 차례 공/수 정보 가져오고 변경
        TurnState currentState = game.getCurrentState();
        game.changeStatus();
        // 현재 라운드 정보 가져오고 변경
        Integer currentRound = game.getCurrentRound();
        game.nextRound();
        gameRepository.save(game);
        GameInfo gameInfo = GameInfo.builder()
                .turnStatus(currentState)
                .currentRound(currentRound)
                .memberId(memberId).build();
        return gameInfo;
    }

    public boolean isEnd(String gameId) {
        // 큐가 비어있다면 게임이 끝난 것
        Optional<Game> findGame = gameRepository.findById(gameId);
        if (findGame.isEmpty()) {
            throw new RoomAndGameNotFoundException("gameId", gameId);
        }
        Game game = findGame.get();
        LinkedList<String> queue = game.getQueue();
        // 비어있다면 점수 비교해서 전적 변경
        return queue.isEmpty();
    }

    public void endGame(String gameId) {
        Optional<Game> findGame = gameRepository.findById(gameId);
        if (findGame.isEmpty()) {
            throw new RoomAndGameNotFoundException("gameId", gameId);
        }
        Game game = findGame.get();
        String winnerId = "";
        String loserId = "";
        if (game.getCreatorScore() > game.getParticipantScore()) {
            winnerId = game.getCreatorId();
            loserId = game.getParticipantId();
        }
        else{
            winnerId = game.getParticipantId();
            loserId = game.getCreatorId();
        }

        // 이긴 사람은 1승 추가 진 사람은 1패 추가
        Optional<Member> findWinner = memberRepository.findByMemberId(winnerId);
        if (findWinner.isEmpty()) {
            throw new MemberNotFoundException(winnerId);
        }
        Optional<Member> findLoser = memberRepository.findByMemberId(loserId);
        if (findLoser.isEmpty()) {
            throw new MemberNotFoundException(loserId);
        }
        Member winner = findWinner.get();
        Member loser = findLoser.get();
        winner.win();
        loser.lose();
        memberRepository.save(winner);
        memberRepository.save(loser);
        SocketMessage winnerMessage = SocketMessage.builder()
                .memberId(winnerId)
                .message("게임에서 승리하셨습니다").build();
        SocketMessage loserMessage = SocketMessage.builder()
                .memberId(loserId)
                .message("게임에서 패배하셨습니다").build();
        template.convertAndSend("/topic/message/" + gameId, winnerMessage);
        template.convertAndSend("/topic/message/" + gameId, loserMessage);
//        template.convertAndSend("/topic/end/" + gameId, "");
    }
}

