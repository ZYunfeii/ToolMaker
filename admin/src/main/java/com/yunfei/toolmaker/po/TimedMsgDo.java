package com.yunfei.toolmaker.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TimedMsgDo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String gmtCreated;
    private String gmtModified;
    private String userName;
    private String timeToSend;
    private String content;
    private Boolean deleted;
    private Boolean sent;
    private String email;
    private Long requestId;
}
