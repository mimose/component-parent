package com.mimose.component.redisson.ratelimit.util;

import org.redisson.api.*;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Description redisson 限流器（令牌桶算法）
 * @Author ccy
 * @Date 2020/2/12
 */
public class RateLimitCreater {

    private static RedissonClient redissonClient;

    public static void setRedissonClient(RedissonClient redissonClient) {
        RateLimitCreater.redissonClient = redissonClient;
    }

    private static RRateLimiter create(String limiter){
        if(ObjectUtils.isEmpty(redissonClient) || StringUtils.isEmpty(limiter)){
            throw new IllegalArgumentException("Incomplete limiter argument");
        }
        return redissonClient.getRateLimiter(limiter);
    }

    public static RRateLimiter create(String limiter, boolean overAll, long qps, long seconds){
        RRateLimiter rRateLimiter = create(limiter);
        if(rRateLimiter.trySetRate(overAll ? RateType.OVERALL : RateType.PER_CLIENT, qps, seconds, RateIntervalUnit.SECONDS)){
            return rRateLimiter;
        }
        throw new IllegalStateException("fail to set RRateLimter");
    }

    public static boolean tryAcquire(String limiter, long ask){
        RRateLimiter rRateLimiter = create(limiter);
        if(rRateLimiter.isExists()){
            return rRateLimiter.tryAcquire(ask);
        }
        return false;
    }

    public static boolean tryAcquire(String limiter, long ask, TimeUnit timeUnit){
        RRateLimiter rRateLimiter = create(limiter);
        if(rRateLimiter.isExists()){
            return rRateLimiter.tryAcquire(ask, timeUnit);
        }
        return false;
    }
}
