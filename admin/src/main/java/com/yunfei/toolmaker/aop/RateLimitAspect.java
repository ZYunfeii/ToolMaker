package com.yunfei.toolmaker.aop;

import com.google.common.util.concurrent.RateLimiter;
import com.yunfei.toolmaker.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, RateLimiter> EXISTED_RATE_LIMITERS = new HashMap<>();

    @Pointcut("@annotation(com.yunfei.toolmaker.annotation.RateLimit)")
    public void rateLimit() {
    }

    @Around(value = "rateLimit()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit annotation = AnnotationUtils.findAnnotation(method, RateLimit.class);
        Class<?> targetClass = point.getTarget().getClass();
        String className = targetClass.getName();
        String key = className + "." + method.getName();
        RateLimiter limiter = EXISTED_RATE_LIMITERS.get(key);
        if (limiter == null) {
            synchronized (this) {
                if (EXISTED_RATE_LIMITERS.get(key) == null) {
                    RateLimiter newLimiter = RateLimiter.create(annotation.limit());
                    EXISTED_RATE_LIMITERS.put(key, newLimiter);
                    limiter = EXISTED_RATE_LIMITERS.get(key);
                } else {
                    limiter = EXISTED_RATE_LIMITERS.get(key);
                }

            }
        }

        // process
        if (limiter.tryAcquire()) {
            return point.proceed();
        } else {
            throw new RuntimeException("too many requests, please try again later...");
        }
    }
}

