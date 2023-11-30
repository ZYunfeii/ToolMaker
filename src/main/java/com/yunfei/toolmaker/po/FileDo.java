package com.yunfei.toolmaker.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class FileDo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long uploadId;
    private String gmtCreated;
    private String gmtModified;
    private byte[] payload;
    private String userName;
    private String fileName;
    private String type;
    private Long size;
    private Boolean deleted;
}
