package com.mimose.component.redisson.lock.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @Description 锁的接口定义类
 * @Author ccy
 * @Date 2019/12/25
 */
public interface DistributedLocker<T extends Lock> {

    T lock(String lockKey);

    T lock(String lockKey, int timeout);

    T lock(String lockKey, TimeUnit unit, int timeout);

    boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime);

    void unlock(String lockKey);

    void unlock(T lock);

}
