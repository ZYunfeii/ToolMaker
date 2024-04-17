package com.yunfei.generator.exception;

import com.yunfei.common.core.domain.R;
import com.yunfei.generator.controller.GenController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.generator.exception
 * @className: GlobalExceptionHandler
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/3/14 20:09
 * @version: 1.0
 */
@ControllerAdvice(assignableTypes = {GenController.class})
public class GlobalExceptionHandler {
    @ExceptionHandler(TargetDataSourceException.class)
    @ResponseBody
    public R handleRequestTerminatedException(TargetDataSourceException ex) {
        String errorMessage = ex.getMessage();
        // 构造适当的错误响应对象
        return R.fail(errorMessage);
    }
}
