package com.yunfei.toolmaker.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class MybatisMapperXmlGenInfo {
    private String daoPath;
    private String entityPath;
    private String tableName;
    private String xmlName;
    @JsonProperty("kv")
    Map<String, String> filedMap;
}
