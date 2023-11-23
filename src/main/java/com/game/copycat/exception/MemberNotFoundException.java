package com.game.copycat.exception;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(String memberId) {
        super("Not found : memberId=".concat(memberId));
    }
}
