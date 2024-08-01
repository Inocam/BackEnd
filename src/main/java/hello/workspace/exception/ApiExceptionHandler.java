package hello.workspace.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

//    @ExceptionHandler(value = CustomException.class)
//    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
//        // 에러에 대한 후처리를 할 수 있습니다. 본 예시에서는 로깅만 진행하였습니다.
//        ErrorResponse errorResponse = new ErrorResponse("Invitation failed", ex.getMessage(), ex.getStatus().value());
//        return new ResponseEntity<>(errorResponse, ex.getStatus());
//
//    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("error : {}, url {}, message : {}",e.getError(), request.getRequestURI(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getError())
                .message(e.getMessage())
                .status(e.getStatus().value())
                .build();

        return new ResponseEntity<>(errorResponse, e.getStatus());
    }
}

