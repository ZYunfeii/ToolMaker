package com.yunfei.toolmaker.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunfei.toolmaker.po.CodeDo;
import com.yunfei.toolmaker.po.FileDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileDao extends BaseMapper<CodeDo> {
    List<FileDo> query(FileDo fileDo);
    void insert(FileDo fileDo);
    void update(FileDo fileDo);
}
