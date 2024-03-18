package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.MybatisMapperXmlGenInfo;
import com.yunfei.toolmaker.service.MybatisXmlGenService;
import com.yunfei.toolmaker.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class CodeGenController {
    @Autowired
    private MybatisXmlGenService mybatisXmlGenService;
    @PostMapping("/codegen/mybatis-mapper")
    public R mapperGen(@RequestBody MybatisMapperXmlGenInfo info) throws IOException {
        String s = mybatisXmlGenService.mapperGenToStream(info);
        return R.ok().put("xml", s);
    }
}
