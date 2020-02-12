package com.mimose.component.redisson.common.config;

import com.mimose.component.redisson.ratelimit.util.RateLimitCreater;
import com.mimose.component.redisson.topic.util.TopicCreater;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;

/**
 * @Description
 * @Author ccy
 * @Date 2020/2/12
 */
@Slf4j
@Configuration
@ConditionalOnBean(value = RedissonClientConfiguration.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 105)
public class RedissonUtilConfiguration {

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void afterClient(){
        startRRateLimiterCreater();
        startTopicCreater();
    }

    private void startRRateLimiterCreater() {
        log.info("start init RRateLimiter creater ...");
        RateLimitCreater.setRedissonClient(redissonClient);
        log.info("start init RRateLimiter creater success");
    }

    private void startTopicCreater() {
        log.info("start init topic creater ...");
        TopicCreater.setRedissonClient(redissonClient);
        log.info("start init topic creater success");
    }
}
