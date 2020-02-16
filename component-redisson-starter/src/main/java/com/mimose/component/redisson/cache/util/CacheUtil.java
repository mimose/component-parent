package com.mimose.component.redisson.cache.util;

import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.util.CollectionUtils;
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

    private static boolean withRedisson = true;

    private static Map<String, ConcurrentMap<String, Object>> cacheMap = new HashMap<>();

    public static void setRedissonClient(RedissonClient redissonClient) {
        CacheUtil.redissonClient = redissonClient;
    }

    public static void noRedisson() {
        CacheUtil.withRedisson = false;
    }

    private static final Integer INITIAL_CAPACITY = 2<<4;

    private static final String HOLD_TIME = "_HOLD_TIME";

    /**
     * RMapCache的MapKey存储Key名
     */
    private static final String RMAPCACHE_MAP_KEYS = "RMAPCACHE_MAP_KEYS";

    /**
     * 是否存在缓存
     * @param mapKey    RMap-key
     * @param cacheKey  RMap-entry-key
     * @return
     */
    public static boolean containKeys(String mapKey, String cacheKey){
        if(withRedisson){
            ConcurrentMap<String, Object> map;
            RSet<String> rMapCacheMapKeys = redissonClient.getSet(RMAPCACHE_MAP_KEYS);
            if(CollectionUtils.isEmpty(rMapCacheMapKeys) || !rMapCacheMapKeys.contains(mapKey)){
                map = redissonClient.getMap(mapKey);
            }else{
                map = redissonClient.getMapCache(mapKey);
            }
            if(!CollectionUtils.isEmpty(map) && (StringUtils.isEmpty(cacheKey) || map.containsKey(cacheKey))){
                cacheMap.put(mapKey, map);
                return true;
            }
            return false;
        }
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
            clearRedisson(mapKey, cacheKey);
        }
    }

    private static void clearRedisson(String mapKey, String cacheKey){
        RSet<String> rMapCacheMapKeys = redissonClient.getSet(RMAPCACHE_MAP_KEYS);
        if(CollectionUtils.isEmpty(rMapCacheMapKeys) || !rMapCacheMapKeys.contains(mapKey)){
            RMap<String, Object> map = redissonClient.getMap(mapKey);
            if(StringUtils.isEmpty(cacheKey)){
                map.clear();
            }else{
                map.remove(cacheKey);
            }
        }else{
            RMapCache<String, Object> mapCache = redissonClient.getMapCache(mapKey);
            if(StringUtils.isEmpty(cacheKey)){
                mapCache.clear();
            }else{
                mapCache.remove(cacheKey);
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
        return AfterGet(mapKey, cacheKey, containKeys(mapKey, cacheKey)? clazz.cast(cacheMap.get(mapKey).get(cacheKey)) : null);
    }

    /**
     * 非redis的情况，存在过期存储的情况，需要判断是否过期，并删除
     * @param mapKey
     * @param cacheKey
     * @param value
     * @param <T>
     * @return
     */
    private static <T> T AfterGet(String mapKey, String cacheKey, T value) {
        if(!withRedisson && !ObjectUtils.isEmpty(value) && validExpiryTime(mapKey, cacheKey)){
            return null;
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
            return AfterGet(mapKey, cacheKey, value.stream().map(clazz::cast).collect(Collectors.toList()));
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
            return AfterGetAll(mapKey, cacheMap.get(mapKey).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, s -> clazz.cast(s.getValue()))));
        }
        return null;
    }

    /**
     * 非redis的情况，存在过期存储的情况，需要判断是否过期，并删除
     * @param mapKey
     * @param value
     * @param <T>
     * @return
     */
    private static <T> Map<String, T> AfterGetAll(String mapKey, Map<String, T> value){
        if(!withRedisson && !ObjectUtils.isEmpty(value)){
            value.entrySet().stream().forEach(es -> {
                if(validExpiryTime(mapKey, es.getKey())){
                    value.remove(mapKey);
                }
            });
        }
        return value;
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
            rMap = withRedisson? redissonClient.getMap(mapKey) : new ConcurrentHashMap<>(INITIAL_CAPACITY);
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
        if(withRedisson){
            RMapCache<String, Object> rMap;
            if(containKeys(mapKey, null)){
                rMap = (RMapCache<String, Object>) cacheMap.get(mapKey);
            }else{
                rMap = redissonClient.getMapCache(mapKey);
                cacheMap.put(mapKey, rMap);
                // 存储RMapCache的mapKey
                RSet<String> rMapCacheMapKeys = redissonClient.getSet(RMAPCACHE_MAP_KEYS);
                rMapCacheMapKeys.add(mapKey);
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

    /**
     * 是否过期
     * @param mapKey
     * @param cacheKey
     * @return
     */
    private static boolean validExpiryTime(String mapKey, String cacheKey) {
        Long expiryTime = containKeys(mapKey, cacheKey + HOLD_TIME)? (Long) cacheMap.get(mapKey).get(cacheKey + HOLD_TIME) : -1L;
        if(expiryTime > 0 && System.currentTimeMillis() > expiryTime){
            clear(mapKey, cacheKey);
            clear(mapKey, cacheKey + HOLD_TIME);
            return true;
        }
        return false;
    }
}
