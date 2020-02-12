package com.mimose.component.redisson.lock.api.client;

import com.mimose.component.redisson.lock.api.DistributedLocker;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 默认java同步锁
 * @Author ccy
 * @Date 2019/12/27
 */
public class DefaultDistributedLocker implements DistributedLocker<ReentrantLock> {

    private ReentrantLock defaultRentrantLock;
    private Map<String, ReentrantLock> defaultLockKeys;

    private ReentrantLock getLock(String lockKey){
        return defaultRentrantLock!=null && defaultLockKeys.containsKey(lockKey) ? defaultLockKeys.get(lockKey) : defaultRentrantLock;
    }

    @Override
    public ReentrantLock lock(String lockKey) {
        ReentrantLock lock = this.getLock(lockKey);
        lock.lock();
        return lock;
    }

    @Override
    public ReentrantLock lock(String lockKey, int timeout) {
        return lock(lockKey, TimeUnit.SECONDS, timeout);
    }

    @Override
    public ReentrantLock lock(String lockKey, TimeUnit unit, int timeout) {
        ReentrantLock lock = this.getLock(lockKey);
        try {
            boolean lock_ = lock.tryLock(timeout, unit);
            return lock_? lock : null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        ReentrantLock lock = this.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unlock(String lockKey) {
        this.unlock(this.getLock(lockKey));
    }

    @Override
    public void unlock(ReentrantLock lock) {
        if(lock.isLocked()){
            lock.unlock();
        }
    }

    public void setDefaultRentrantLock(ReentrantLock defaultRentrantLock) {
        this.defaultRentrantLock = defaultRentrantLock;
    }

    public void setLockKeys(Map<String, ReentrantLock> defaultLockKeys) {
        this.defaultLockKeys = defaultLockKeys;
    }
}
