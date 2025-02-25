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

//    private final SimpMessagingTemplate simpMessagingTemplate;
//    private final SimpUserRegistry simpUserRegistry;
//
//    @MessageExceptionHandler(MethodArgumentNotValidException.class)
//    @SendToUser(value = "/user/subscribe/errors", broadcast = false)
//    public void handleValidationException(MethodArgumentNotValidException ex, Principal principal) {
//        ErrorReasonDTO e = ErrorStatus._BAD_REQUEST.getReasonHttpStatus();
//        ApiResponse<Object> response = ApiResponse.onFailure(
//                e.getCode(),
//                ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
//                null
//        );
//        SimpUser simpUser = simpUserRegistry.getUser(principal.getName());
//
//        simpMessagingTemplate.convertAndSendToUser(
//                simpUser.getName(),
//                "/subscribe/errors",
//                response
//        );
//    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser("/subscribe/errors")
    public ApiResponse<Object> handleValidationException(MethodArgumentNotValidException ex) {  // Principal 파라미터 제거
        ErrorReasonDTO e = ErrorStatus._BAD_REQUEST.getReasonHttpStatus();
        return ApiResponse.onFailure(  // void 대신 ApiResponse 반환
                e.getCode(),
                ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
                null
        );
    }
}