package com.yunfei.toolmaker.service.impl;

import com.yunfei.toolmaker.config.FreeMarkerConfig;
import com.yunfei.toolmaker.dto.MybatisMapperXmlGenInfo;
import com.yunfei.toolmaker.service.MybatisXmlGenService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "codegen.mybatis-mapper")
@Service
public class MybatisXmlGenServiceImpl implements MybatisXmlGenService {
    private String templateName;

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public void mapperGenToStream(OutputStream outputStream, MybatisMapperXmlGenInfo mybatisMapperXmlGenInfo) {
        Configuration configuration = FreeMarkerConfig.getConfig();
        Template template;
        try {
            template = configuration.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 准备模板数据
        Map<String, Object> data = new HashMap<>();

        data.put("filedMap", mybatisMapperXmlGenInfo.getFiledMap());
        data.put("daoPath", mybatisMapperXmlGenInfo.getDaoPath());
        data.put("entityPath", mybatisMapperXmlGenInfo.getEntityPath());
        data.put("tableName", mybatisMapperXmlGenInfo.getTableName());

        // 渲染模板并输出到控制台
        try {
            template.process(data, new OutputStreamWriter(outputStream));
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
