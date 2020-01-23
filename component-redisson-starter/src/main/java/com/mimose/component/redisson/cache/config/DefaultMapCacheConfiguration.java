package com.mimose.component.redisson.cache.config;

import com.mimose.component.redisson.cache.util.CacheUtil;
import com.mimose.component.redisson.common.config.RedissonClientConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;

/**
 * @Description 静态块基本存取配置类
 * @Author ccy
 * @Date 2019/12/27
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(value = RedissonClientConfiguration.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 101)
public class DefaultMapCacheConfiguration {

    @PostConstruct
    public void afterClient(){
        log.info("start init default map cache util ...");
        CacheUtil.setRedissonClient(null);
        CacheUtil.noRedisson();
        log.info("start init default map cache util success");
    }
}
