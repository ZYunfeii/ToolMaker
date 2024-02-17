package com.yunfei.toolmaker.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class VideoController {

    @GetMapping("/video")
    @ResponseBody
    public ResponseEntity<Resource> getVideo() throws IOException {
        // 加载视频文件
        Resource videoFile = new ClassPathResource("video/test.mp4");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));

        // 返回视频流
        return ResponseEntity.ok()
                .headers(headers)
                .body(videoFile);
    }
}
