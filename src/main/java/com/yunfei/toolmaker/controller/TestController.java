package com.yunfei.toolmaker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class TestController {

    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session) {

        return "login";
    }
}
