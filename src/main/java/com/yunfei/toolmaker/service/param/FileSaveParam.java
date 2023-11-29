package com.yunfei.toolmaker.service.param;

import lombok.Data;

@Data
public class FileSaveParam {
    private byte[] payload;
    private String userName;
    private String fileName;
    private String type;
    private Long size;
}
