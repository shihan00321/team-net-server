package com.teamnet.team_net.global.exception.handler;

import com.teamnet.team_net.global.exception.GeneralException;
import com.teamnet.team_net.global.response.code.BaseErrorCode;

public class PostHandler extends GeneralException {
    public PostHandler(BaseErrorCode code) {
        super(code);
    }
}
