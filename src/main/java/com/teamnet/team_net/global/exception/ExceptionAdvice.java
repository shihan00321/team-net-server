package com.teamnet.team_net.global.exception;

import com.teamnet.team_net.global.response.ApiResponse;
import com.teamnet.team_net.global.response.code.ErrorReasonDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> onThrowException(GeneralException generalException) {
        ErrorReasonDTO e = generalException.getErrorReasonHttpStatus();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.onFailure(e.getCode(), e.getMessage()));
    }
}
