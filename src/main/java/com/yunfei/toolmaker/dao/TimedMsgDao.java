package com.yunfei.toolmaker.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunfei.toolmaker.po.TimedMsgDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TimedMsgDao extends BaseMapper<TimedMsgDo> {
    List<TimedMsgDo> query(TimedMsgDo timedMsgDo);
    void updateTimedTask(TimedMsgDo timedMsgDo);
    List<TimedMsgDo> selectAll();
}
