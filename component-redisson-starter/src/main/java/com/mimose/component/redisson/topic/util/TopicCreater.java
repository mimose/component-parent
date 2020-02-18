package com.mimose.component.redisson.topic.util;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @Description
 * @Author ccy
 * @Date 2020/2/12
 */
public class TopicCreater {

    private static RedissonClient redissonClient;

    public static void setRedissonClient(RedissonClient redissonClient) {
        TopicCreater.redissonClient = redissonClient;
    }

    public static RTopic create(String topic){
        if(ObjectUtils.isEmpty(redissonClient) || StringUtils.isEmpty(topic)){
            throw new IllegalArgumentException("Incomplete topic argument");
        }
        return redissonClient.getTopic(topic);
    }

    public static long publish(String topic, Object message){
        return create(topic).publish(message);
    }

}
