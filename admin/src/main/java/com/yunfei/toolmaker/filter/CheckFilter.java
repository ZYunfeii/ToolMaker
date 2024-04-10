package com.yunfei.toolmaker.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker.filter
 * @className: CheckFilter
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/3/29 15:33
 * @version: 1.0
 */
@Slf4j
@WebFilter(filterName = "checkFilter", urlPatterns = "/*")
public class CheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求:{}", req.getRequestURI());
        filterChain.doFilter(req, response); // 放行
    }
}
