package com.yunfei.toolmaker.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChunkFinishedInfo {
    private String filename;
    private String key;
    private String uploadId;
    @Data
    private static class Part {
        private Integer partNumber;
        private String eTag;
    }
    private List<Part> partList;
}
