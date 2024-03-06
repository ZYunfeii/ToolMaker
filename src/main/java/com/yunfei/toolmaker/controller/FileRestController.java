package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.component.NonStaticResourceHttpRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

@RestController
@Slf4j
public class FileRestController {

    @Autowired
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @GetMapping("/video")
    public void getVideo(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info(headerName + ":" + request.getHeader(headerName));
        }

        //sourcePath 是获取编译后 resources 文件夹的绝对地址，获得的原始 sourcePath 以/开头，所以要用 substring(1) 去掉第一个字符/
        //realPath 即视频所在的完整地址
        String os = System.getProperty("os.name").toLowerCase();
        String sourcePath = this.getClass().getClassLoader().getResource("").getPath();
        String realPath = null;
        if (os.contains("win")) {
            sourcePath = sourcePath.substring(1);
            realPath = sourcePath + "video/" + "video.mp4";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            realPath = "./video/video.mp4";
        }

        Path filePath = Paths.get(realPath);
        log.info(realPath);
        if (Files.exists(filePath)) {
            log.info("There exist file!");
            // 利用 Files.probeContentType 获取文件类型
            String mimeType = Files.probeContentType(filePath);
            if (!StringUtils.isEmpty(mimeType)) {
                // 设置 response
                response.setContentType(mimeType);
            }
            request.setAttribute(nonStaticResourceHttpRequestHandler.filepath, filePath);
            // 利用 ResourceHttpRequestHandler.handlerRequest() 实现返回视频流
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }
}

