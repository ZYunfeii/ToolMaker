package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.TimedMsgSubmitData;
import com.yunfei.toolmaker.dto.UserRegisterInfo;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.service.FileService;
import com.yunfei.toolmaker.service.param.FileSaveParam;
import com.yunfei.toolmaker.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

import static com.yunfei.toolmaker.constant.AuthServerConstant.LOGIN_USER;

@RestController
public class UploadController {
    @Autowired
    private FileService fileService;
    @PostMapping(value ="/upload/file", consumes = "multipart/form-data")
    public ResponseEntity<R> fileUpload(@RequestParam("file") MultipartFile file, HttpSession session) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(R.error(ErrorCodeEnum.FILE_UPLOAD_EMPTY_ERROR.getCode(), ErrorCodeEnum.FILE_UPLOAD_EMPTY_ERROR.getMsg()), HttpStatus.BAD_REQUEST);
        }
        try {
            // 获取文件的payload（内容）
            byte[] fileBytes = file.getBytes();

            // 获取文件的大小
            long fileSize = file.getSize();

            // 获取文件的类型
            String fileType = file.getContentType();

            // 获取文件的名称
            String fileName = file.getOriginalFilename();

            Object attribute = session.getAttribute(LOGIN_USER);
            UserRegisterInfo userRegisterInfo = (UserRegisterInfo) attribute;


            FileSaveParam fileSaveParam = new FileSaveParam();
            fileSaveParam.setFileName(fileName);
            fileSaveParam.setSize(fileSize);
            fileSaveParam.setType(fileType);
            fileSaveParam.setPayload(fileBytes);
            fileSaveParam.setUserName(userRegisterInfo.getId());
            fileService.saveFile(fileSaveParam);

            return new ResponseEntity<>(R.ok(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(R.error(ErrorCodeEnum.FILE_UPLOAD_ERROR.getCode(), ErrorCodeEnum.FILE_UPLOAD_ERROR.getMsg()), HttpStatus.BAD_REQUEST);
        }
    }
}
