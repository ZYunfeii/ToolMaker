package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.po.UserDo;
import com.yunfei.toolmaker.service.UserService;
import com.yunfei.toolmaker.util.R;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestController {

    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session) {

        return "login";
    }

    @Autowired
    private UserService userService;

    class UserNameAndPassword {
        public String username;
        public String password;
    }
    @ResponseBody
    @GetMapping("/api/info")
    public R testProtalApi() {
        UserDo userDo = new UserDo();
        List<UserDo> res = userService.select(userDo);
        List<UserNameAndPassword> userNameAndPasswordList = new ArrayList<>();
        for (UserDo re : res) {
            UserNameAndPassword userNameAndPassword = new UserNameAndPassword();
            userNameAndPassword.password = re.getPassword();
            userNameAndPassword.username = re.getName();
            userNameAndPasswordList.add(userNameAndPassword);
        }
        R ok = R.ok();
        ok.setData(userNameAndPasswordList);
        return ok;
    }
}
