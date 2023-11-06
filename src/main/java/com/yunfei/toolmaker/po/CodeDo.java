package com.yunfei.toolmaker.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CodeDo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String gmtCreated;
    private String gmtModified;
    private String userName;
    private String code;
    private String runResult;
    private Boolean deleted;

}
