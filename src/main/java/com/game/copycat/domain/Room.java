package com.game.copycat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Room {
    @Id
    private String id; // 현재 시간 + 랜덤 문자열
    @NotBlank
    private String roomName;
    @NotNull
    private String password;
    @NotNull
    private Boolean isLocked;
    @NotNull
    private Integer round;

    @Builder
    public Room(String roomName, String password, boolean isLocked, Integer round) {
        String current = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String randomString = RandomStringUtils.randomAlphabetic(4);
        this.id = current + randomString;
        this.roomName = roomName;
        this.password = password;
        this.isLocked = isLocked;
        this.round = round;
    }
}
