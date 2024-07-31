package com.sparta.backend.chat.global;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatus(), ex.getError(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    @Getter
    public static class ErrorResponse {
        private final int status;
        private final ErrorCode error;
        private final String message;

        public ErrorResponse(int status, ErrorCode error, String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}

