package com.mimose.component.redisson.common.config;

import com.mimose.component.redisson.cache.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;

/**
 * @Description 基本工具类初始化（无redisson）
 * @Author ccy
 * @Date 2020/2/18
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(value = RedissonClientConfiguration.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 105)
public class DefaultNoRedissonUtilConfiguration {

    @PostConstruct
    public void afterClient(){
        configDefaultMapCache();
    }

    /**
     * 普通静态Map 基本存取配置类
     */
    private void configDefaultMapCache() {
//        log.info("start init default map cache util ...");
        CacheUtil.setRedissonClient(null);
        CacheUtil.noRedisson();
        log.info("start init default map cache util success");
    }
}
