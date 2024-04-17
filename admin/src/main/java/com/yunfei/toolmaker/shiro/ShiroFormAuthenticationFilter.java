package com.yunfei.toolmaker.shiro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker.shiro
 * @className: ShiroFormAuthenticationFilter
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/4/17 10:02
 * @version: 1.0
 */
@Slf4j
public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }
                //allow them to see the login page ;)
                return true;
            }
        } else {
            HttpServletRequest req = (HttpServletRequest)request;
            HttpServletResponse resp = (HttpServletResponse) response;
            if(req.getMethod().equals(RequestMethod.OPTIONS.name())) {
                resp.setStatus(HttpStatus.OK.value());
                return true;
            }

            if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [" + getLoginUrl() + "]");
            }

            //前端Ajax请求，则不会重定向
            resp.setHeader("Access-Control-Allow-Origin",  req.getHeader("Origin"));
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setContentType("application/json; charset=utf-8");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            R r = R.error(ErrorCodeEnum.SHIRO_NOT_LOGIN.getCode(), ErrorCodeEnum.SHIRO_NOT_LOGIN.getMsg());
            out.println(JSON.toJSONString(r));
            out.flush();
            out.close();

//            saveRequestAndRedirectToLogin(request, response);
            return false;
        }
    }
}
