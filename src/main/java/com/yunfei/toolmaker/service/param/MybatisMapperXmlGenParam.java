package com.yunfei.toolmaker.service.param;

import lombok.Data;

import java.util.Map;

@Data
public class MybatisMapperXmlGenParam {
    private String daoPath;
    private String entityPath;
    private String tableName;
    Map<String, String> filedMap;
}
