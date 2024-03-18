package com.yunfei.toolmaker.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunfei.toolmaker.dto.UserRegisterInfo;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.po.UserDo;
import com.yunfei.toolmaker.service.UserService;
import com.yunfei.toolmaker.util.HttpUtils;
import com.yunfei.toolmaker.util.R;
import com.yunfei.toolmaker.util.SnowFlake;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/user/oauth2/login")
    public RedirectView loginOAuth2() throws Exception {
        String clientId = "b62e8e471a9f97822601";
        String redirectUrl = String.format("https://github.com/login/oauth/authorize?client_id=%s", clientId);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    private Map<Long, UserRegisterInfo> crosDomainSessionSetMap = new HashMap<>(5);
    @GetMapping("/setsession")
    public ResponseEntity<R> crosSetSession(@RequestParam("sessionid") Long sessionId, HttpSession session) {
        if (!crosDomainSessionSetMap.containsKey(sessionId)) {
            return new ResponseEntity<>(R.error(ErrorCodeEnum.SET_SESSION_ERROR.getCode(), ErrorCodeEnum.SET_SESSION_ERROR.getMsg()), HttpStatus.BAD_REQUEST);
        }
        session.setAttribute(LOGIN_USER, crosDomainSessionSetMap.get(sessionId));
        crosDomainSessionSetMap.remove(sessionId);
        return new ResponseEntity<>(R.ok(), HttpStatus.OK);
    }
    @GetMapping("/oauth2/callback")
    public RedirectView oauth2Callback(@RequestParam("code") String code) throws Exception {
        Map<String, String> map = new HashMap<>(5);
        map.put("client_id", "b62e8e471a9f97822601");
        map.put("client_secret", "25ae22c585a9c8217763ec34415ce30d767ca2a6");
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://github.com", "/login/oauth/access_token", "post", new HashMap<>(), map, new HashMap<>());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
        // 在响应体中提取access token
        String accessToken = extractAccessToken(responseBody);
        System.out.println(accessToken);
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Bearer " + accessToken);
        HttpResponse userGithubInfoResponse = HttpUtils.doGet("https://api.github.com", "/user", "get", headers, new HashMap<>());
        String githubUserInfo = EntityUtils.toString(userGithubInfoResponse.getEntity());

        // 使用FastJSON解析JSON字符串
        JSONObject jsonObject = JSON.parseObject(githubUserInfo);

        // 获取id字段的值
        String id = jsonObject.getString("login");

        UserRegisterInfo userRegisterInfo = new UserRegisterInfo();
        userRegisterInfo.setId(id);

        Long sessionSetId = SnowFlake.getInstance().nextId();
        crosDomainSessionSetMap.put(sessionSetId, userRegisterInfo);


        String redirectUrl = String.format("http://localhost:3000/admin.html?sessionsetid=%s", sessionSetId);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }
    private static String extractAccessToken(String responseBody) {
        // 假设响应体是一个通过&连接的key=value形式的字符串
        // 这里使用简单的字符串处理方法来提取access token
        // 实际情况请根据实际响应的结构进行调整

        String[] params = responseBody.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("access_token")) {
                return keyValue[1];
            }
        }

        return null; // 如果无法提取access token，则返回null或抛出异常
    }
}