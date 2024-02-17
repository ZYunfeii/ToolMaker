package com.yunfei.toolmaker.controller;


import com.yunfei.toolmaker.dto.UserRegisterInfo;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.po.UserDo;
import com.yunfei.toolmaker.service.UserService;
import com.yunfei.toolmaker.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static com.yunfei.toolmaker.constant.AuthServerConstant.LOGIN_USER;
@Api(value = "Auth Interfaces", tags = "Auth Interfaces")
@RestController
public class AuthController {
    @Autowired
    UserService userService;
    @ApiOperation("register")
    @ApiImplicitParam(name = "info", type = "body", dataTypeClass = UserRegisterInfo.class, required = true)
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterInfo info) {
        if (userService.exist(info.getId())) {
            return R.error(ErrorCodeEnum.USER_EXIST_EXCEPTION.getCode(), ErrorCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        } else {
            UserDo userDo = new UserDo();
            userDo.setName(info.getId());
            userDo.setPassword(info.getPassword());
            userService.insertUser(userDo);
            return R.ok();
        }
    }
    @ApiOperation("static/login")
    @ApiImplicitParam(name = "info", type = "body", dataTypeClass = UserRegisterInfo.class, required = true)
    @PostMapping("/login")
    public R login(@RequestBody UserRegisterInfo info, HttpSession session) {
        if (!userService.exist(info.getId())) {
            return R.error(ErrorCodeEnum.USER_LOGIN_EXCEPTION.getCode(), ErrorCodeEnum.USER_LOGIN_EXCEPTION.getMsg());
        }
        if (!userService.passwordMatch(info.getId(), info.getPassword())) {
            return R.error(ErrorCodeEnum.USER_LOGIN_PASSWORD_NOT_MATCH_EXCEPTION.getCode(),
                    ErrorCodeEnum.USER_LOGIN_PASSWORD_NOT_MATCH_EXCEPTION.getMsg());
        }
        session.setAttribute(LOGIN_USER, info);
        return R.ok();
    }
    @ApiOperation("get user info")
    @GetMapping("/user/info")
    public R getUserInfo(HttpSession session) {
        Object attribute = session.getAttribute(LOGIN_USER);
        if (attribute == null) {
            return null;
        } else {
            UserRegisterInfo userRegisterInfo = (UserRegisterInfo) attribute;
            return R.ok().put("userinfo", userRegisterInfo);

        }
    }
}