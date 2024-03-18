package com.yunfei.toolmaker.exception;

public enum ErrorCodeEnum {
    USER_EXIST_EXCEPTION(500, "用户已经存在"),
    USER_LOGIN_EXCEPTION(500, "请先注册"),
    USER_LOGIN_PASSWORD_NOT_MATCH_EXCEPTION(500, "密码不正确"),
    USER_NOT_LOGIN(500, "请先登录"),
    USER_NOT_ADMIN(500, "您不是管理员"),
    TIMED_MSG_PARA_ERROR(500, "参数不正确"),
    SET_SESSION_ERROR(500, "session id不存在"),
    FILE_UPLOAD_EMPTY_ERROR(500, "上传文件为空"),
    FILE_UPLOAD_ERROR(500, "上传文件后端处理错误");
    private final int code;
    private final String msg;
    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
