package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.dto.MybatisMapperXmlGenInfo;

import java.io.OutputStream;

public interface MybatisXmlGenService {
    String mapperGenToStream(MybatisMapperXmlGenInfo mybatisMapperXmlGenInfo);
}
