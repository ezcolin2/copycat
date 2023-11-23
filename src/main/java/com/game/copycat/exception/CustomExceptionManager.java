package com.game.copycat.exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class CustomExceptionManager {
    @ExceptionHandler(RoomAndGameNotFoundException.class)
    public String notFoundException(RoomAndGameNotFoundException ex) {
        return "redirect:/rooms";
    }
    @ExceptionHandler(MemberNotFoundException.class)
    public String memberNotFoundException(MemberNotFoundException ex) {
        return "redirect:/login";
    }
}
