package roomescape.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("입력값 유효성 검증 실패: {}", errorMessage, ex);
        ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getErrorCode(), errorMessage));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("데이터베이스 제약 조건 위반", ex);
        ErrorCode errorCode = GlobalErrorCode.DATA_INTEGRITY_VIOLATION;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getErrorCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.warn("필수 요청 헤더 누락", ex);
        ErrorCode errorCode = GlobalErrorCode.MISSING_REQUEST_HEADER;
        String message = "필수 요청 헤더 '" + ex.getHeaderName() + "'가 누락되었습니다.";

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getErrorCode(), message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("JSON 형식 오류", ex);
        ErrorCode errorCode = GlobalErrorCode.INVALID_REQUEST_BODY;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getErrorCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("타입 변환 오류", ex);
        ErrorCode errorCode = GlobalErrorCode.INVALID_TYPE_VALUE;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getErrorCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
        log.warn("예상치 못한 서버 내부 에러 발생!", ex);
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getErrorCode(), errorCode.getMessage()));
    }
}
