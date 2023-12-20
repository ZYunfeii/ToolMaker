package com.yunfei.toolmaker.controller;

import com.alibaba.fastjson.JSON;
import com.yunfei.toolmaker.dto.UserRegisterInfo;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.service.UserService;
import com.yunfei.toolmaker.util.FileUtils;
import com.yunfei.toolmaker.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.yunfei.toolmaker.constant.AuthServerConstant.LOGIN_USER;
@Controller
public class LogController {
    @Autowired
    private UserService userService;
    private static final String FILE_PATH = "./logs/info.log";
    @GetMapping("/logs")
    public ResponseEntity<StreamingResponseBody> handleLog(HttpSession session) throws IOException {
        Object attribute = session.getAttribute(LOGIN_USER);
        if (attribute == null) {
            return new ResponseEntity<>(makeErrorStreamingResponseBody(ErrorCodeEnum.USER_NOT_LOGIN), HttpStatus.OK);
        }
        UserRegisterInfo userRegisterInfo = (UserRegisterInfo) attribute;
        if (!userService.checkAdmin(userRegisterInfo.getId())) {
            return new ResponseEntity<>(makeErrorStreamingResponseBody(ErrorCodeEnum.USER_NOT_ADMIN), HttpStatus.OK);
        }

        StreamingResponseBody streamingOfFile = FileUtils.getStreamingOfFile(FILE_PATH);
        return new ResponseEntity<>(streamingOfFile, HttpStatus.OK);
    }

    private StreamingResponseBody makeErrorStreamingResponseBody(ErrorCodeEnum codeEnum) {
        StreamingResponseBody stream = out -> {
            R r = R.error(codeEnum.getCode(), codeEnum.getMsg());
            out.write(JSON.toJSONString(r).getBytes());
            out.flush();
        };
        return stream;
    }
}
