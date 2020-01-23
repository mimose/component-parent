package com.mimose.component.redisson.lock.config;

import com.mimose.component.redisson.common.config.RedissonClientConfiguration;
import com.mimose.component.redisson.lock.api.DistributedLocker;
import com.mimose.component.redisson.lock.api.client.RedissonDistributedLocker;
import com.mimose.component.redisson.lock.util.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @Description redisson锁装配
 * @Author ccy
 * @Date 2019/12/25
 */
@Slf4j
@Configuration
@ConditionalOnBean(value = RedissonClientConfiguration.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 102)
public class RedissonLockConfiguration {

    /**
     * 装配locker类，并将实例注入到LockUtil中
     * @return
     */
    @Bean
    DistributedLocker distributedLocker(RedissonClient redissonClient) {
        try{
            log.info("start redisson lock ...");
            RedissonDistributedLocker locker = new RedissonDistributedLocker();
            locker.setRedissonClient(redissonClient);
            LockUtil.setLocker(locker);
            log.info("start redisson lock success");
            return locker;
        }catch (final Exception e){
            log.error("start redisson lock error", e);
            return null;
        }
    }
}
