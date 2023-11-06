package com.yunfei.toolmaker.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserDo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String gmtCreated;
    private String gmtModified;
    private String name;
    private String password;
    private Boolean deleted;
    private Boolean adminFlag;
}
