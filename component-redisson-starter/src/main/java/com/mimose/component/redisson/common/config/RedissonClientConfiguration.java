package com.mimose.component.redisson.common.config;

import com.mimose.component.redisson.common.util.RedissonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @Description redisson客户端装配
 * @Author ccy
 * @Date 2019/12/25
 */
@Slf4j
@Configuration
@ConditionalOnClass(Config.class)
@ConditionalOnProperty(name = RedissonConstants.OPEN_REDIS, havingValue = RedissonConstants.OPEN)
@EnableConfigurationProperties(RedissonBaseProperties.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 50)
public class RedissonClientConfiguration {

    /**
     * RedissonClient装配
     * @return
     */
    @Bean
    public RedissonClient redissonClient(RedissonBaseProperties redissonBaseProperties) {
        try {
            Config config = new Config();
            if(StringUtils.isNotBlank(redissonBaseProperties.getMasterName())){
                // 哨兵模式自动装配
                log.info("start redisson client with sentinel server ...");
                SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redissonBaseProperties.getSentinelAddresses())
                        .setMasterName(redissonBaseProperties.getMasterName())
                        .setTimeout(redissonBaseProperties.getTimeout())
                        .setConnectTimeout(redissonBaseProperties.getConnectionTimeout())
                        .setMasterConnectionPoolSize(redissonBaseProperties.getMasterConnectionPoolSize())
                        .setSlaveConnectionPoolSize(redissonBaseProperties.getSlaveConnectionPoolSize());

                if(StringUtils.isNotBlank(redissonBaseProperties.getPassword())) {
                    serverConfig.setPassword(redissonBaseProperties.getPassword());
                }
            }else if(StringUtils.isNotBlank(redissonBaseProperties.getAddress())){
                // 单机模式自动装配
                log.info("start redisson client with single server ...");
                SingleServerConfig serverConfig = config.useSingleServer()
                        .setAddress(redissonBaseProperties.getAddress())
                        .setTimeout(redissonBaseProperties.getTimeout())
                        .setConnectTimeout(redissonBaseProperties.getConnectionTimeout())
                        .setConnectionPoolSize(redissonBaseProperties.getConnectionPoolSize())
                        .setConnectionMinimumIdleSize(redissonBaseProperties.getConnectionMinimumIdleSize());

                if(StringUtils.isNotBlank(redissonBaseProperties.getPassword())) {
                    serverConfig.setPassword(redissonBaseProperties.getPassword());
                }
            }
            RedissonClient redissonClient = Redisson.create(config);
            log.info("start redisson client success");
            return redissonClient;
        } catch (Exception e) {
            log.error("start redisson client error", e);
            return null;
        }
    }

}
