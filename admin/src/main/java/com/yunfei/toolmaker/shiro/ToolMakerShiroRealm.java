package com.yunfei.toolmaker.shiro;

import com.yunfei.toolmaker.po.UserDo;
import com.yunfei.toolmaker.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker.shiro
 * @className: ToolMakerShiroRealm
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/4/16 10:43
 * @version: 1.0
 */
@Component
public class ToolMakerShiroRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //authenticationToken.getPrincipal()是获得用户名
        if (authenticationToken.getPrincipal() == null) {
            throw new AuthenticationException("账号名为空，登录失败！");
        }
        //获取用户信息
        String name = authenticationToken.getPrincipal().toString();
        UserDo userDo = new UserDo();
        userDo.setName(name);
        List<UserDo> res = userService.select(userDo);
        if (res.isEmpty()) {
            //这里返回后会报出对应异常
            throw new AuthenticationException("不存在的账号，登录失败！");
        } else if (res.size() > 1) {
            throw new AuthenticationException("多个匹配账户名，登录失败！");
        } else {
            //这里验证authenticationToken和simpleAuthenticationInfo的信息
            //getName()  是Shiro包下org.apache.shiro.realm.CachingRealm的方法，不是自定义的
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(name, res.get(0).getPassword(), getName());
            return simpleAuthenticationInfo;
        }
    }
}
