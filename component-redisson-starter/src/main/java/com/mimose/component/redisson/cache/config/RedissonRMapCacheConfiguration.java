package com.mimose.component.redisson.cache.config;

import com.mimose.component.redisson.cache.util.CacheUtil;
import com.mimose.component.redisson.common.config.RedissonClientConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Description redisson基本存取配置类
 * @Author ccy
 * @Date 2019/12/27
 */
@Slf4j
@Configuration
@ConditionalOnBean(value = RedissonClientConfiguration.class)
public class RedissonRMapCacheConfiguration {

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void afterClient(){
        log.info("start init rMap cache util ...");
        CacheUtil.setRedissonClient(redissonClient);
        log.info("start init rMap cache util success");
    }
}
