package com.mimose.component.redisson.cache.config;

import com.mimose.component.redisson.common.config.RedissonClientConfiguration;
import com.mimose.component.redisson.common.util.RedissonConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.net.URL;

/**
 * @Description redisson缓存装配
 * @Author ccy
 * @Date 2019/12/25
 */
@Slf4j
@Configuration
@ConditionalOnBean(value = RedissonClientConfiguration.class)
@EnableConfigurationProperties(RedissonCacheProperties.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 100)
public class RedissonCacheConfiguration {

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient, RedissonCacheProperties redissonCacheProperties) {
        try {
            log.info("start redisson cache manager ...");
            String configLocation = redissonCacheProperties == null || StringUtils.isEmpty(redissonCacheProperties.getConfig()) ? RedissonConstants.DEFAULT_CACHE_CONFIGLOCATION : redissonCacheProperties.getConfig();
            if(ObjectUtils.isEmpty(this.getClass().getClassLoader().getResource(
                    configLocation.substring(
                            configLocation.indexOf(RedissonConstants.DEFAULT_CONFIGLOCATION_PREFIX)+ RedissonConstants.DEFAULT_CONFIGLOCATION_PREFIX.length())))){
                log.info("start redisson cache manager fail, file[{}] not exist", configLocation);
                return null;
            }
            RedissonSpringCacheManager redissonSpringCacheManager = new RedissonSpringCacheManager(redissonClient, configLocation);
            URL resource = this.getClass().getClassLoader().getResource("redisson-cache.yml");
            log.info("start redisson cache manager success");
            return redissonSpringCacheManager;
        } catch (Exception e) {
            log.info("start redisson cache manager error", e);
            return null;
        }
    }

}
