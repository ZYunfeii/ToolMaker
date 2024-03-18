package com.yunfei.toolmaker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/test.html")
    public String getTestHtml() {
        return "test"; // 返回名为"page1"的视图
    }
}
