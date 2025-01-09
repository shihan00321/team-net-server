package com.teamnet.team_net.global.exception;

import com.teamnet.team_net.global.response.ApiResponse;
import com.teamnet.team_net.global.response.code.ErrorReasonDTO;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

public abstract class BaseExceptionHandler extends ResponseEntityExceptionHandler {

    // 스프링이 제공하는 404 예외
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorReasonDTO e = ErrorStatus._NOT_FOUND.getReasonHttpStatus();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.onFailure(e.getCode(), e.getMessage(), null));
    }

    //MethodArgumentNotValidException DTO Validation 검증
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        ErrorReasonDTO e = ErrorStatus._BAD_REQUEST.getReasonHttpStatus();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.onFailure(e.getCode(), e.getMessage(), fieldErrors));
    }

}
