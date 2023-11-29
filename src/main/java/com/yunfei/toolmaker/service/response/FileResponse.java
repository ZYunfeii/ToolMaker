package com.yunfei.toolmaker.service.response;

import lombok.Data;

@Data
public class FileResponse {
    private byte[] payload;
    private String userName;
    private String fileName;
    private String type;
    private Long size;
}
