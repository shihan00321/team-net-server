package com.teamnet.team_net.global.exception;

import com.teamnet.team_net.global.response.ApiResponse;
import com.teamnet.team_net.global.response.code.ErrorReasonDTO;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class ExceptionAdvice extends BaseExceptionHandler {

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> onThrowException(GeneralException generalException) {
        ErrorReasonDTO e = generalException.getErrorReasonHttpStatus();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.onFailure(e.getCode(), e.getMessage(), null));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> onThrowRuntimeException() {
        ErrorReasonDTO e = ErrorStatus._INTERNAL_SERVER_ERROR.getReasonHttpStatus();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.onFailure(e.getCode(), e.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException() {
        ErrorReasonDTO e = ErrorStatus._FORBIDDEN.getReasonHttpStatus();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.onFailure(e.getCode(), e.getMessage(), null));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException() {
        ErrorReasonDTO e = ErrorStatus._UNAUTHORIZED.getReasonHttpStatus();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.onFailure(e.getCode(), e.getMessage(), null));
    }
}
