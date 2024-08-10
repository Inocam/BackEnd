package com.sparta.backend.workspace.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private String error;
    private String message;
    private int status;

}

