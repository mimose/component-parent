package com.mimose.component.redisson.lock.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimose.component.redisson.common.util.RedissonConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @Description 默认锁机制 所需配置项
 * @Author ccy
 * @Date 2019/12/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = RedissonConstants.DEFAULT_LOCK_CONFIG_PREFIX)
public class DefaultLockProperties implements Serializable {

    private static final long serialVersionUID = -1967103621974075621L;

    @JsonProperty(RedissonConstants.DEFAULT_LOCK_CONFIG_PREFIX + ".enumPackUrl")
    private String enumPackUrl;
}
