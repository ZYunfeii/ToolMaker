package com.yunfei.toolmaker.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunfei.toolmaker.po.CodeDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface CodeDao extends BaseMapper<CodeDo> {
    List<CodeDo> query(CodeDo codeDo);
    int insert(CodeDo codeDo);
}
