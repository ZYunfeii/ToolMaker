package com.yunfei.toolmaker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ToolmakerThreadPoolConfig {
//    @Bean
//    public ThreadPoolExecutor threadPoolExecutor() {
//        return new ThreadPoolExecutor(
//                3,
//                8,
//                20,
//                TimeUnit.SECONDS,
//                new LinkedBlockingDeque<>(100000),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy()
//        );
//    }

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(2);
    }
}
