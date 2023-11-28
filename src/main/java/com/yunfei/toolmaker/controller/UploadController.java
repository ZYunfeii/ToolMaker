package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.TimedMsgSubmitData;
import com.yunfei.toolmaker.util.R;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class UploadController {
    @PostMapping(value ="/upload/file", consumes = "multipart/form-data")
    public ResponseEntity<R> fileCheck(@RequestParam("file") MultipartFile file, HttpSession session) {
        return null;
    }
}
