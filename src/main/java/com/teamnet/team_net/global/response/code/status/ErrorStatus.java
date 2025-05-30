package com.teamnet.team_net.global.response.code.status;

import com.teamnet.team_net.global.response.code.BaseErrorCode;
import com.teamnet.team_net.global.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "존재하지 않는 페이지입니다."),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "지원하지 않는 메서드입니다."),

    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    MEMBER_NICKNAME_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4003", "이미 존재하는 닉네임입니다."),

    // 게시글 관련 에러
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST4001", "게시글을 찾을 수 없습니다."),
    POST_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "POST4003", "게시글에 대한 권한이 없습니다."),

    // 팀 관련 에러
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM4001", "해당 팀을 찾을 수 없습니다."),
    TEAM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM4002", "해당 팀의 멤버가 아닙니다."),
    TEAM_MEMBER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "TEAM4003", "해당 팀에 대한 권한이 없습니다."),
    TEAM_NAME_EXIST(HttpStatus.BAD_REQUEST, "TEAM4004", "이미 존재하는 팀 이름입니다."),

    // 알림 관련 에러
    NOTIFICATION_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NOTI4001", "알림 연결 중 에러가 발생했습니다."),

    // 댓글 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT4001", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMENT4002", "해당 댓글에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
