package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.dto.MybatisMapperXmlGenInfo;

import java.io.OutputStream;

public interface MybatisXmlGenService {
    void mapperGenToStream(OutputStream outputStream, MybatisMapperXmlGenInfo mybatisMapperXmlGenInfo);
}
