package com.teamnet.team_net.global.exception.handler;

import com.teamnet.team_net.global.exception.GeneralException;
import com.teamnet.team_net.global.response.code.BaseErrorCode;

public class MemberHandler extends GeneralException {
    public MemberHandler(BaseErrorCode code) {
        super(code);
    }
}
