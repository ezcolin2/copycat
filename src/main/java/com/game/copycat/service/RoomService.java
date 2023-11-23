package com.game.copycat.service;

import com.game.copycat.domain.Room;
import com.game.copycat.dto.RoomRequest;
import com.game.copycat.exception.RoomAndGameNotFoundException;
import com.game.copycat.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room createRoom(RoomRequest request) {
        // 이후 Room id를 통해 방 삭제를 하기 위해 id가 포함된 엔티티를 넘겨준다.
        System.out.println("request = " + request.getIsLocked());
        if (request.getIsLocked() == null && request.getPassword() == null) {
            Room room = Room.builder()
                    .roomName(request.getRoomName())
                    .password("")
                    .round(request.getRound())
                    .isLocked(false)
                    .build();
            return roomRepository.save(room);

        }
        Room room = Room.builder()
                .roomName(request.getRoomName())
                .password(request.getPassword())
                .round(request.getRound())
                .isLocked(request.getIsLocked().equals("true"))
                .build();
        return roomRepository.save(room);
    }

    public Room findById(String id) {
        Optional<Room> findRoom = roomRepository.findById(id);
        if (findRoom.isEmpty()) {
            throw new RoomAndGameNotFoundException("roomId", id);
        }
        return findRoom.get();
    }

    public boolean passwordCheck(String id, String password) {
        Optional<Room> room = roomRepository.findById(id);
        return room.get().getPassword().equals(password);
    }

    public void deleteRoom(String id) {
        // Room은 따로 삭제 버튼을 두지 않고 방에 사람이 없으면 자동으로 삭제
        Optional<Room> find = roomRepository.findById(id);
        if (find.isPresent()) {
            roomRepository.delete(find.get());
        }
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Room enterRoom(String id) {
        Room room = roomRepository.findById(id).get();
        room.enterRoom();
        return roomRepository.save(room);
    }
    public Room leaveRoom(String id) {
        Room room = roomRepository.findById(id).get();
        Integer currentNum = room.getCurrentNum();
        // 마지막 남은 유저가 떠나면 삭제함
        if (currentNum == 1) {
            roomRepository.delete(room);
            return null;
        }
        room.leaveRoom();
        return roomRepository.save(room);
    }
}
