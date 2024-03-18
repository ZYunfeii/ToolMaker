package com.yunfei.generator.dto;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.generator.dto
 * @className: DataSourceConfig
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/3/14 11:38
 * @version: 1.0
 */
import lombok.Data;
import javax.validation.constraints.NotBlank;
@Data
public class DataSourceDTO {
    @NotBlank
    private String driverClassName;
    @NotBlank
    private String url;
    @NotBlank
    private String username;
    private String password;
}