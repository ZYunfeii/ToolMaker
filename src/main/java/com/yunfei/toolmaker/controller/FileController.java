package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.ChunkFinishedInfo;
import com.yunfei.toolmaker.dto.StartChunkInfo;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.service.FileService;
import com.yunfei.toolmaker.service.param.FileSaveParam;
import com.yunfei.toolmaker.service.response.FileResponse;
import com.yunfei.toolmaker.service.response.FilesInfoResponse;
import com.yunfei.toolmaker.util.R;
import com.yunfei.toolmaker.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RestController
public class FileController extends BaseController {
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

            FileSaveParam fileSaveParam = new FileSaveParam();
            fileSaveParam.setFileName(fileName);
            fileSaveParam.setSize(fileSize);
            fileSaveParam.setType(fileType);
            fileSaveParam.setPayload(fileBytes);
            fileSaveParam.setUserName(getUserName(session));
            fileSaveParam.setUploadId(SnowFlake.getInstance().nextId());
            fileService.saveFile(fileSaveParam);

            return new ResponseEntity<>(R.ok(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(R.error(ErrorCodeEnum.FILE_UPLOAD_ERROR.getCode(), ErrorCodeEnum.FILE_UPLOAD_ERROR.getMsg()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filelist")
    public R getFilesInfo(HttpSession session) {
        String userName = getUserName(session);
        FilesInfoResponse filesInfoWithUserName = fileService.getFilesInfoWithUserName(userName);
        return R.ok().setData(filesInfoWithUserName.getFileInfoList());
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename, HttpSession session) {
        FileResponse response = fileService.getFileBytesWithFileName(filename, getUserName(session));

        HttpHeaders headers = new HttpHeaders();
        if (!response.getUserName().equals(getUserName(session))) {
            return ResponseEntity.badRequest().headers(headers).body("File is not for you!".getBytes());
        }
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(response.getSize())
                .body(response.getPayload());
    }

    @DeleteMapping("/delete/{filename}")
    public R deleteFile(@PathVariable String filename, HttpSession session) {
        if (!fileService.getUserNameFromFileName(filename).equals(getUserName(session))) {
            return R.error("File is not for you!");
        }
        fileService.deleteFile(filename);
        return R.ok("delete successfully!");
    }

    @PostMapping("/upload/file/startChunkApi")
    public ResponseEntity<Map> startChunk(@RequestBody StartChunkInfo startChunkInfo) {
        Map<String, Object> m = new HashMap<>();
        if (fileService.existByFileName(startChunkInfo.getFilename())) {
            m.put("error", "filename has already exist.");
            return new ResponseEntity<>(m, HttpStatus.BAD_REQUEST);
        }
        m.put("key", "");
        m.put("uploadId", String.valueOf(SnowFlake.getInstance().nextId()));
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    private Map<Long, Integer> partNumberMap = new HashMap<>(12);

    private static class FileChunk {
        public long fileSize;
        public String fileType;
        public String fileName;
        public byte[] payload;
    }
    private Map<Long, FileChunk> fileBytesMap = new HashMap<>(6);
    @PostMapping(value = "/upload/file/chunkApi", consumes = "multipart/form-data")
    public R chunkApi(@RequestParam("file") MultipartFile file, @RequestParam("partNumber") Integer partNumber,
                      @RequestParam("uploadId") String uploadId,
                      HttpSession session) throws IOException, IllegalAccessException, InterruptedException {
        Map<String, Object> m = new HashMap<>();
        m.put("eTag", String.valueOf(SnowFlake.getInstance().nextId()));

        // 获取文件的payload（内容）
        byte[] fileBytes = file.getBytes();

        // 获取文件的大小
        long fileSize = file.getSize();

        // 获取文件的类型
        String fileType = file.getContentType();

        // 获取文件的名称
        String fileName = file.getOriginalFilename();

        synchronized (this) {
            if (partNumber == 1) {
                partNumberMap.put(Long.valueOf(uploadId), 1);
                FileChunk fileChunk = new FileChunk();
                fileChunk.fileName = fileName;
                fileChunk.fileSize = fileSize;
                fileChunk.fileType = fileType;
                fileChunk.payload = fileBytes;
                fileBytesMap.put(Long.valueOf(uploadId), fileChunk);
            } else {
                Long id = Long.valueOf(uploadId);
                while (partNumberMap.get(id) == null || partNumberMap.get(id) + 1 != partNumber) {
                    wait();
                }
                partNumberMap.put(id, partNumberMap.get(id) + 1);
                FileChunk fileChunk = fileBytesMap.get(id);
                fileChunk.fileSize += fileSize;

                byte[] newPayload = Arrays.copyOf(fileChunk.payload, fileChunk.payload.length + fileBytes.length);
                System.arraycopy(fileBytes, 0, newPayload, fileChunk.payload.length, fileBytes.length);

                fileChunk.payload = newPayload;
            }

            notifyAll();
        }

        return R.ok().setData(m);
    }

    @PostMapping("/upload/file/finishChunkApi")
    public R finishChunkApi(@RequestBody ChunkFinishedInfo chunkFinishedInfo, HttpSession session) throws IllegalAccessException {
        Map<String, Object> m = new HashMap<>();
        m.put("value", "");
        Long uploadId = Long.valueOf(chunkFinishedInfo.getUploadId());
        FileChunk fileChunk = fileBytesMap.get(uploadId);
        FileSaveParam fileSaveParam = new FileSaveParam();
        fileSaveParam.setFileName(fileChunk.fileName);
        fileSaveParam.setSize(fileChunk.fileSize);
        fileSaveParam.setType(fileChunk.fileType);
        fileSaveParam.setPayload(fileChunk.payload);
        fileSaveParam.setUserName(getUserName(session));
        fileSaveParam.setUploadId(uploadId);
        fileService.saveFile(fileSaveParam);

        partNumberMap.remove(uploadId);
        fileBytesMap.remove(uploadId);
        return R.ok().setData(m);
    }

}
