package com.yunfei.toolmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class ToolMakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolMakerApplication.class, args);
    }

}
