package com.game.copycat.service;

import com.game.copycat.domain.Room;
import com.game.copycat.dto.RoomRequest;
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

    public Room enterRoom(String id) {
        return roomRepository.findById(id).get();
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

}
