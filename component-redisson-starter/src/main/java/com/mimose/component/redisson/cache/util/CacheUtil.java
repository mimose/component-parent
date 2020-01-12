package com.mimose.component.redisson.cache.util;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description 基本存储数据工具类
 * @Author ccy
 * @Date 2019/12/27
 */
public class CacheUtil {

    private static RedissonClient redissonClient;

    private static Map<String, ConcurrentMap<String, Object>> cacheMap = new HashMap<>();

    private static final Integer INITIAL_CAPACITY = 2<<4;

    private static final String HOLD_TIME = "_HOLD_TIME";

    public static void setRedissonClient(RedissonClient redissonClient) {
        CacheUtil.redissonClient = redissonClient;
    }

    /**
     * 是否存在缓存
     * @param mapKey    RMap-key
     * @param cacheKey  RMap-entry-key
     * @return
     */
    public static boolean containKeys(String mapKey, String cacheKey){
        return cacheMap.containsKey(mapKey) && (StringUtils.isEmpty(cacheKey) || cacheMap.get(mapKey).containsKey(cacheKey));
    }

    /**
     * 清空缓存
     * @param mapKey
     * @param cacheKey
     */
    public static void clear(String mapKey, String cacheKey){
        AssertMapKey(mapKey);
        if(containKeys(mapKey, cacheKey)){
            if(StringUtils.isEmpty(cacheKey)){
                cacheMap.remove(mapKey);
            }else{
                cacheMap.get(mapKey).remove(cacheKey);
            }
        }
    }

    /**
     * 获取缓存
     * @param mapKey
     * @param cacheKey
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T get(String mapKey, String cacheKey, Class<T> clazz){
        AssertMapKey(mapKey);
        return AfterGet(cacheKey, containKeys(mapKey, cacheKey)? clazz.cast(cacheMap.get(mapKey).get(cacheKey)) : null);
    }

    /**
     * 非redis的情况，存在过期存储的情况，需要判断是否过期
     * @param cacheKey
     * @param t
     * @param <T>
     * @return
     */
    private static <T> T AfterGet(String cacheKey, T value) {
        if(redissonClient == null && !ObjectUtils.isEmpty(value)){
            Long expiryTime = (Long) cacheMap.get(cacheKey).get(cacheKey + HOLD_TIME);
            if(System.currentTimeMillis() > expiryTime){
                value = null;
            }
        }
        return value;
    }

    /**
     * 获取List缓存对象
     * @param mapKey
     * @param cacheKey
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> getVIsList(String mapKey, String cacheKey, Class<T> clazz){
        AssertMapKey(mapKey);
        if(containKeys(mapKey, cacheKey)){
            List<Object> value = ArrayList.class.cast(cacheMap.get(mapKey).get(cacheKey));
            return value.stream().map(clazz::cast).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 获取所有缓存
     * @param mapKey
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getAll(String mapKey, Class<T> clazz){
        AssertMapKey(mapKey);
        if(containKeys(mapKey, null)){
            return cacheMap.get(mapKey).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, s -> clazz.cast(s.getValue())));
        }
        return null;
    }

    /**
     * 设置缓存
     * @param mapKey
     * @param cacheKey
     * @param value
     */
    public static void put(String mapKey, String cacheKey, Object value){
        AssertMapKey(mapKey);
        ConcurrentMap<String, Object> rMap;
        if(containKeys(mapKey, null)){
            rMap = cacheMap.get(mapKey);
        }else{
            rMap = redissonClient == null ? new ConcurrentHashMap<>(INITIAL_CAPACITY) : redissonClient.getMap(mapKey);
            cacheMap.put(mapKey, rMap);
        }
        rMap.put(cacheKey, value);
    }

    /**
     * 设置缓存（带过期时间）
     * @param mapKey
     * @param cacheKey
     * @param value
     * @param ttl   过期时间
     * @param unit  时间类型
     */
    public static void put(String mapKey, String cacheKey, Object value, Long ttl, TimeUnit unit){
        AssertMapKey(mapKey);
        if(redissonClient != null){
            RMapCache<String, Object> rMap;
            if(containKeys(mapKey, null)){
                rMap = (RMapCache<String, Object>) cacheMap.get(mapKey);
            }else{
                rMap = redissonClient.getMapCache(mapKey);
                cacheMap.put(mapKey, rMap);
            }
            rMap.put(cacheKey, value, ttl, unit);
        }else{
            ConcurrentHashMap<String, Object> cMap;
            if(containKeys(mapKey, null)){
                cMap = (ConcurrentHashMap<String, Object>) cacheMap.get(mapKey);
            }else{
                cMap = new ConcurrentHashMap<>(INITIAL_CAPACITY);
                cacheMap.put(mapKey, cMap);
            }
            cMap.put(cacheKey, value);
            cMap.put(cacheKey + HOLD_TIME, System.currentTimeMillis() + unit.toMillis(ttl));
        }
    }

    /**
     * 是否存在mapKey
     * @param mapKey
     */
    private static void AssertMapKey(String mapKey){
        if(StringUtils.isEmpty(mapKey)){
            throw new IllegalArgumentException("mapkey can't be null");
        }
    }

}
