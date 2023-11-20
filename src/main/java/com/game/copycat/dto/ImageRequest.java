package com.game.copycat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageRequest {
    private String roomId;
    private String base64Image;

    @Builder
    public ImageRequest(String roomId, String base64Image) {
        this.roomId = roomId;
        this.base64Image = base64Image;
    }
}
