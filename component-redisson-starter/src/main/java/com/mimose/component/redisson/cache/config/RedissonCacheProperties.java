package com.mimose.component.redisson.cache.config;

import com.mimose.component.redisson.common.util.RedissonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description redisson 缓存配置信息
 * @Author ccy
 * @Date 2019/12/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = RedissonConstants.DEFAULT_CACHE_CONFIG_PREFIX)
public class RedissonCacheProperties {

    /** 配置文件 **/
    private String config;

}
