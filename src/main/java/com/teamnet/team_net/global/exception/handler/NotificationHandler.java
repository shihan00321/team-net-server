package com.teamnet.team_net.global.exception.handler;

import com.teamnet.team_net.global.exception.GeneralException;
import com.teamnet.team_net.global.response.code.BaseErrorCode;

public class NotificationHandler extends GeneralException {

    public NotificationHandler(BaseErrorCode code) {
        super(code);
    }
}
