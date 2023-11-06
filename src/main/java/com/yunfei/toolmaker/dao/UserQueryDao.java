package com.yunfei.toolmaker.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.yunfei.toolmaker.po.UserDo;

import java.util.List;

@Mapper
public interface UserQueryDao extends BaseMapper<UserDo> {
    List<UserDo> query(UserDo user);

    @Override
    int insert(UserDo user);
}
