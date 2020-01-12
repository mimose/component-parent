package com.mimose.component.redisson.common.util;

/**
 * @Description redisson 常量类
 * @Author ccy
 * @Date 2019/12/26
 */
public class RedissonConstants {

    /** REDISSON OPEN OR NOT OPEN true/false **/
    public static final String OPEN_REDIS = "redisson.open";
    /** REDISSON OPEN **/
    public static final String OPEN = "true";


    /** REDISSON BASE CONFIG PREFIX **/
    public static final String DEFAULT_BASE_CONFIG_PREFIX = "redisson.base";
    /** REDISSON CACHE CONFIG PREFIX **/
    public static final String DEFAULT_CACHE_CONFIG_PREFIX = "redisson.cache";
    /** REDISSON LOCK CONFIG PREFIX **/
    public static final String DEFAULT_LOCK_CONFIG_PREFIX = "redisson.lock";
    /** REDISSON CACHE DEFAULT CONFIG LOCATION **/
    public static final String DEFAULT_CACHE_CONFIGLOCATION = "classpath:redisson-cache.yml";
    /** REDISSON LOCK DEFAULT CONFIG LOCATION **/
    public static final String DEFAULT_LOCK_CONFIG_YAML_LOCATION = "classpath:redisson-default-lock.yml";


    /** REDISSON TIMEOUT 命令等待超时，单位：毫秒 **/
    public static final int DEFAULT_REDISSON_TIMEOUT = 5000;
    /** REDISSON TIMEOUT 连接超时时间，单位：毫秒 **/
    public static final int DEFAULT_REDISSON_CONNECTION_TIMEOUT = 5000;
    /** REDISSON DATABASE 数据库编号 **/
    public static final int DEFAULT_REDISSON_DATABASE = 0;
    /** REDISSON CONNECTION_POOL_SIZE 连接池大小 **/
    public static final int DEFAULT_REDISSON_CONNECTION_POOL_SIZE = 250;
    /** REDISSON CONNECTION_MINIUMUM_IDLE_SIZE 最小空闲连接数 **/
    public static final int DEFAULT_REDISSON_CONNECTION_MINIUMUM_IDLE_SIZE = 20;
    /** REDISSON SLAVE_CONNECTION_POOL_SIZE 从节点连接池大小 **/
    public static final int DEFAULT_REDISSON_SLAVE_CONNECTION_POOL_SIZE = 400;
    /** REDISSON MASTER_CONNECTION_POOL_SIZE 主节点连接池大小 **/
    public static final int DEFAULT_REDISSON_MASTER_CONNECTION_POOL_SIZE = 400;
}
