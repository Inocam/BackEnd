package com.sparta.backend.workspace.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// CustomException 을 생성하면, 해당 에러와 관련된 메시지에 추가로 httpStatus 도 같이 전달가능
@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final String error;
    private final String message;

    public CustomException(HttpStatus status, String error, String message) {
        super(message);
        this.status = status;
        this.error = error;
        this.message = message;
    }
    public HttpStatus getStatus() {
        return status;
    }
}
