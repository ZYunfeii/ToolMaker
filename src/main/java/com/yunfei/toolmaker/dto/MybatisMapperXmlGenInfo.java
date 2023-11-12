package com.yunfei.toolmaker.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MybatisMapperXmlGenInfo {
    private String daoPath;
    private String entityPath;
    private String tableName;
    Map<String, String> filedMap;
}
