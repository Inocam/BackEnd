package com.sparta.backend.chat.global;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final int status;
    private final ErrorCode error;
    private final String message;

    public CustomException(int status, ErrorCode error, String message) {
        super(message);
        this.status = status;
        this.error = error;
        this.message = message;
    }
}