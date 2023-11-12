package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.service.MybatisXmlGenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class CodeGenController {
    @Autowired
    private MybatisXmlGenService mybatisXmlGenService;
    @PostMapping("/codegen/mybatis-mapper")
    public void mapperGen(HttpServletResponse response) {

    }
}
