package com.game.copycat.exception;

import lombok.Getter;

@Getter
public class RoomAndGameNotFoundException extends RuntimeException{
    public RoomAndGameNotFoundException(String field, String value) {
        super("Not Found : ".concat(field).concat("=").concat(value));
    }
}
