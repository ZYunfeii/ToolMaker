package com.yunfei.toolmaker.service.param;

import lombok.Data;

@Data
public class TimedMsgSubmitParam {
    private String userName;
    private String timeToSend;
    private String content;
    private String email;
}
