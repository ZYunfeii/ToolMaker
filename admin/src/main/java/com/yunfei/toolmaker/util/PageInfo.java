package com.yunfei.toolmaker.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker.util
 * @className: PageInfo
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/4/17 16:43
 * @version: 1.0
 */
@Data
@AllArgsConstructor
public class PageInfo {
    private Integer totalRows;
    private Integer currentPage;
    private Integer pageSize;
    private Object data;
}
