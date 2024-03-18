package com.yunfei.toolmaker.service.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class FilesInfoResponse {
    @Data
    public static class FileInfo{
        private String fileName;
        private String type;
        // MB
        private Double size;
    }
    private List<FileInfo> fileInfoList = new ArrayList<>();
}
