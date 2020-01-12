package com.mimose.component.redisson.common.config;

import com.mimose.component.redisson.common.util.RedissonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description redisson 基础配置信息
 * @Author ccy
 * @Date 2019/12/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = RedissonConstants.DEFAULT_BASE_CONFIG_PREFIX)
public class RedissonBaseProperties {

    private int timeout = RedissonConstants.DEFAULT_REDISSON_TIMEOUT;

    private int connectionTimeout = RedissonConstants.DEFAULT_REDISSON_CONNECTION_TIMEOUT;

    private String address;

    private String password;

    private int database = RedissonConstants.DEFAULT_REDISSON_DATABASE;

    private int connectionPoolSize = RedissonConstants.DEFAULT_REDISSON_CONNECTION_POOL_SIZE;

    private int connectionMinimumIdleSize = RedissonConstants.DEFAULT_REDISSON_CONNECTION_MINIUMUM_IDLE_SIZE;

    private int slaveConnectionPoolSize = RedissonConstants.DEFAULT_REDISSON_SLAVE_CONNECTION_POOL_SIZE;

    private int masterConnectionPoolSize = RedissonConstants.DEFAULT_REDISSON_MASTER_CONNECTION_POOL_SIZE;

    private String[] sentinelAddresses;

    private String masterName;

}
