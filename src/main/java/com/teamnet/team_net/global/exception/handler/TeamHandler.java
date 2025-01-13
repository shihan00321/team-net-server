package com.teamnet.team_net.global.exception.handler;

import com.teamnet.team_net.global.exception.GeneralException;
import com.teamnet.team_net.global.response.code.BaseErrorCode;

public class TeamHandler extends GeneralException {

    public TeamHandler(BaseErrorCode code) {
        super(code);
    }
}
