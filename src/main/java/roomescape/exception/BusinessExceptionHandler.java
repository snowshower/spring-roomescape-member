package roomescape.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "roomescape")
public class BusinessExceptionHandler {

    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<ErrorResponse> handleRoomescapeException(RoomescapeException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("비즈니스 예외 발생: {} - {}", errorCode.getErrorCode(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getErrorCode(), errorCode.getMessage()));
    }
}
