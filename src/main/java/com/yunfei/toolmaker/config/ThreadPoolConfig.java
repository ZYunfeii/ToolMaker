package com.yunfei.toolmaker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
//    @Bean
//    public ThreadPoolExecutor threadPoolExecutor() {
//        return new ThreadPoolExecutor(
//                4,
//                8,
//                60,
//                TimeUnit.SECONDS,
//                new LinkedBlockingDeque<>(100000),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy()
//        );
//    }

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }
}
