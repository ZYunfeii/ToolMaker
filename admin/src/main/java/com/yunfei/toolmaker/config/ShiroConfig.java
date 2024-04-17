package com.yunfei.toolmaker.config;

import com.yunfei.toolmaker.shiro.ShiroFormAuthenticationFilter;
import com.yunfei.toolmaker.shiro.ToolMakerShiroRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.Filter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker.config
 * @className: ShiroConfig
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/4/16 19:56
 * @version: 1.0
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ToolMakerShiroRealm myShiroRealm() {
        ToolMakerShiroRealm myShiroRealm = new ToolMakerShiroRealm();
        return myShiroRealm;
    }

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheManager();
    }

    @Bean
    public SessionDAO sessionDAO() {
        return new EnterpriseCacheSessionDAO();
    }

    @Bean
    public SessionManager sessionManager(SessionDAO sessionDAO) {
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        manager.setSessionDAO(sessionDAO);
        //设置session过期时间
        manager.setGlobalSessionTimeout(3600000);
        manager.setSessionValidationInterval(3600000);
        return manager;
    }

    /**
     * 权限管理，配置主要是Realm的管理认证
     */
    @Bean
    public SecurityManager securityManager(CacheManager cacheManager, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setRealm(myShiroRealm());
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }

    /**
     * Filter工厂，设置对应的过滤条件和跳转条件  这是重点！！！
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //必须采用LinkedHashMap有序存储过滤条件
        Map<String, String> map = new LinkedHashMap<>();
        //anon表示所有用户都可以不鉴权匿名访问

        //此路径必须放在最后  这是为啥一定使用LinkedHashMap的原因
        map.put("/api/login", "anon");
        map.put("/api/blog/list", "anon");
        map.put("/api/**", "authc");
        //登录
        shiroFilterFactoryBean.setLoginUrl("/login");

        LinkedHashMap<String, Filter> filtsMap=new LinkedHashMap<String, Filter>();
        filtsMap.put("authc",new ShiroFormAuthenticationFilter() );
        shiroFilterFactoryBean.setFilters(filtsMap);
        //首页
//        shiroFilterFactoryBean.setSuccessUrl("/index");
//        shiroFilterFactoryBean.setUnauthorizedUrl("/api/login");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    /**
     * 加入shiro注解的使用，不加入这个注解不生效
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }


}
