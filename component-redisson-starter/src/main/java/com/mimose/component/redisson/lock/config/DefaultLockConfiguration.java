package com.mimose.component.redisson.lock.config;

import com.google.common.collect.Lists;
import com.mimose.component.redisson.lock.api.client.DefaultDistributedLocker;
import com.mimose.component.redisson.lock.util.LockUtil;
import com.mimose.component.redisson.common.config.RedissonClientConfiguration;
import com.mimose.component.redisson.lock.api.DistributedLocker;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description reentrantlock锁装配
 * @Author ccy
 * @Date 2019/12/27
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(value = RedissonClientConfiguration.class)
@EnableConfigurationProperties(DefaultLockProperties.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 102)
public class DefaultLockConfiguration implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Bean
    DefaultLockKeyBean defaultLockKeyBean(DefaultLockProperties defaultLockProperties){
        try{
            log.info("start init default lock key ...");
            List<String> keys = getKeys(defaultLockProperties.getEnumPackUrl());
            if(CollectionUtils.isEmpty(keys)){
                return new DefaultLockKeyBean(null);
            }
            Map<String, ReentrantLock> lockKeys = keys.stream().collect(Collectors.toMap(Function.identity(), s -> new ReentrantLock()));
            log.info("start init default lock key success, keys: [{}]", keys.stream().collect(Collectors.joining(",")));
            return new DefaultLockKeyBean(lockKeys);
        } catch (final Exception e){
            log.error("start init default lock key error", e);
            return new DefaultLockKeyBean(null);
        }
    }

    /**
     * 装配locker类，并将实例注入到LockUtil中
     * @return
     */
    @Bean
    @ConditionalOnBean(DefaultLockKeyBean.class)
    DistributedLocker distributedLocker(DefaultLockKeyBean defaultLockKeyBean) {
        try{
            log.info("start default reentrant lock ...");
            if(defaultLockKeyBean == null || CollectionUtils.isEmpty(defaultLockKeyBean.getReentrantLockMap())){
                throw new RuntimeException("default reentrant lock is empty");
            }
            DefaultDistributedLocker locker = new DefaultDistributedLocker();
            locker.setDefaultRentrantLock(new ReentrantLock());
            locker.setLockKeys(defaultLockKeyBean.getReentrantLockMap());
            LockUtil.setLocker(locker);
            log.info("start default reentrant lock success");
            return locker;
        }catch (final Exception e){
            log.error("start default reentrant lock error", e);
            return null;
        }
    }

    /**
     * 有配置路径时
     * @param packUrl
     * @return
     */
    private List getKeys(String packUrl) {
        if(StringUtils.isEmpty(packUrl)){
            packUrl = DefaultLockPropertiesBuilder.newDefaultLockPropertiesFromYAML().getEnumPackUrl();
            if(StringUtils.isEmpty(packUrl)){
                // 无配置路径，扫描包下元数据中名称包含 *ock*.class 的枚举类
                return this.getKeysWhenNoProperties();
            }
        }
        List<String> keys = new ArrayList<>();
        Reflections reflections = new Reflections(packUrl);
        Set<Class<? extends DefaultLockKey>> types = reflections.getSubTypesOf(DefaultLockKey.class);
        if(types!=null && types.size()>0){
            types.stream().forEach(type->{
                try{
                    DefaultLockKey[] locks = type.getEnumConstants();
                    if(locks != null && locks.length > 0){
                        Arrays.stream(locks).forEach(lock -> {
                            keys.add(lock.getName());
                        });
                    }
                }catch (final Exception e){
                    log.error("start init default lock key--getKeys error", e);
                }
            });
            keys.removeIf(Objects::isNull);
        }
        return keys;
    }

    /**
     * 无配置路径时
     * @return
     * @throws IOException
     */
    private List getKeysWhenNoProperties() {
        log.info("start init default lock key ---> Find the class whose name contains the '*ock*.class'");
        try {
            List<String> keys = new ArrayList<>();
            // 扫描元数据
            ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
            MetadataReaderFactory cachingReader = new CachingMetadataReaderFactory(resourceLoader);
            Resource[] resources = resolver.getResources("classpath*:**/*ock*.class");
            String lockKeySubName = DefaultLockKey.class.getName();
            Collections.synchronizedList(Arrays.asList(resources)).parallelStream().filter(resource -> {
                try {
                    // 扫描到DefaultLockKey的子类
                    return Arrays.asList(cachingReader.getMetadataReader(resource).getClassMetadata().getInterfaceNames()).contains(lockKeySubName);
                } catch (IOException e) {
                    return false;
                }
            }).forEach(resource -> {
                try {
                    // 初始化锁
                    MetadataReader reader = cachingReader.getMetadataReader(resource);
                    Class<DefaultLockKey> aClass = (Class<DefaultLockKey>) Class.forName(reader.getClassMetadata().getClassName());
                    DefaultLockKey[] locks = aClass.getEnumConstants();
                    if(locks != null && locks.length > 0){
                        Arrays.stream(locks).forEach(lock -> {
                            keys.add(lock.getName());
                        });
                    }
                } catch (IOException | ClassNotFoundException e) {
                    log.error("start init default lock key ---> Find the class whose name contains the '*ock*.class' error", e);
                }
            });
            keys.removeIf(Objects::isNull);
            return keys;
        } catch (IOException e) {
            log.error("start init default lock key ---> Find the class whose name contains the '*ock*.class' totally error", e);
            return null;
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
