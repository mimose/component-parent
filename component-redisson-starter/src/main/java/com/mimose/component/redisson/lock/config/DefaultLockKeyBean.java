package com.mimose.component.redisson.lock.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 默认锁机制 所需锁key的bean类
 * @Author ccy
 * @Date 2019/12/27
 */
@Getter
@AllArgsConstructor
public class DefaultLockKeyBean {

    private Map<String, ReentrantLock> reentrantLockMap;

}
