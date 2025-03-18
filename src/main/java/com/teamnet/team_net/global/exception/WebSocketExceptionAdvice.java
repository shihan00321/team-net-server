package com.teamnet.team_net.global.exception;

import com.teamnet.team_net.global.response.ApiResponse;
import com.teamnet.team_net.global.response.code.ErrorReasonDTO;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@RequiredArgsConstructor
@Slf4j
@ControllerAdvice
public class WebSocketExceptionAdvice {

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser("/subscribe/errors")
    public ApiResponse<Object> handleValidationException(MethodArgumentNotValidException ex) {  // Principal 파라미터 제거
        ErrorReasonDTO e = ErrorStatus._BAD_REQUEST.getReasonHttpStatus();
        return ApiResponse.onFailure(
                e.getCode(),
                ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
                null
        );
    }
}