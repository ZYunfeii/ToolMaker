package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.UserRegisterInfo;

import javax.servlet.http.HttpSession;

import static com.yunfei.toolmaker.constant.AuthServerConstant.LOGIN_USER;

public abstract class BaseController {
    protected String getUserName(HttpSession session) {
        Object attribute = session.getAttribute(LOGIN_USER);
        UserRegisterInfo userRegisterInfo = (UserRegisterInfo) attribute;
        return userRegisterInfo.getId();
    }
}
