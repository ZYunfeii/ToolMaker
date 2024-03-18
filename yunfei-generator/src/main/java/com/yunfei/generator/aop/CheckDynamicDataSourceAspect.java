package com.yunfei.generator.aop;

import com.yunfei.generator.annotation.CheckDynamicDataSource;
import com.yunfei.generator.controller.DataSourceController;
import com.yunfei.generator.exception.TargetDataSourceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.generator.aop
 * @className: CheckDynamicDataSourceAspect
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/3/14 19:55
 * @version: 1.0
 */
@Aspect
@Component
public class CheckDynamicDataSourceAspect {
    @Autowired
    private DataSourceController dataSourceController;
    @Before("@annotation(checkDynamicDataSource)")
    public void doBefore(CheckDynamicDataSource checkDynamicDataSource) throws Throwable {
        if (!dataSourceController.existTargetDataSource()) {
            throw new TargetDataSourceException("待扫描的目标数据库信息未设置！");
        }
    }
}
