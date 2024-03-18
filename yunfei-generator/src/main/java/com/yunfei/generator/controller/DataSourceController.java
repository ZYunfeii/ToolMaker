package com.yunfei.generator.controller;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.generator.controller
 * @className: DataSourceController
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/3/14 15:44
 * @version: 1.0
 */
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.*;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.yunfei.common.core.domain.R;
import com.yunfei.generator.dto.DataSourceDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.Set;

@RestController
@RequestMapping("/datasources")
public class DataSourceController {
    public static final String poolName = "yunfei-pool";

    //动态数据源
    @Autowired
    private DynamicRoutingDataSource drds;
    //数据源创建器
    @Autowired
    private BasicDataSourceCreator dataSourceCreator;

    //创建数据源
    @PostMapping("/add")
    public R createDataSource(DataSourceDTO vo) {
        DataSourceProperty dsp = new DataSourceProperty();
        dsp.setPoolName(poolName);//链接池名称
        dsp.setUrl(vo.getUrl());//数据库连接
        dsp.setUsername(vo.getUsername());//用户名
        dsp.setPassword(vo.getPassword());//密码
        dsp.setDriverClassName(vo.getDriverClassName());//驱动
        //创建数据源并添加到系统中管理
        DataSource dataSource = null;
        try {
            dataSource = dataSourceCreator.createDataSource(dsp);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        drds.addDataSource(poolName, dataSource);
        return R.ok(null, "设置成功！");
    }

    @GetMapping
    public Set<String> getDataSourceKeys() {
        return drds.getDataSources().keySet();
    }

    public Boolean existTargetDataSource() {
        return getDataSourceKeys().contains(poolName);
    }

}
