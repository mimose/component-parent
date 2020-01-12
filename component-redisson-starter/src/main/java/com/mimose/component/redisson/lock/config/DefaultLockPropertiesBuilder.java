package com.mimose.component.redisson.lock.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mimose.component.redisson.common.util.RedissonConstants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

/**
 * @Description
 * @Author ccy
 * @Date 2019/12/31
 */
public class DefaultLockPropertiesBuilder {

    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * 通过默认的yml文件来获取DefaultLockProperties属性
     * @return
     */
    public static DefaultLockProperties newDefaultLockPropertiesFromYAML() {
        try {
            ClassPathResource classPathResource = new ClassPathResource(RedissonConstants.DEFAULT_LOCK_CONFIG_YAML_LOCATION.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length()), ClassUtils.getDefaultClassLoader());
            DefaultLockProperties properties = yamlMapper.readValue(classPathResource.getInputStream(), new TypeReference<DefaultLockProperties>() {
            });
            if(properties == null){
//                throw new RuntimeException("get default lock packurl error");
                return DefaultLockProperties.builder().build();
            }
            return properties;
        } catch (IOException e) {
//            throw new RuntimeException("get default lock packurl error", e);
            return DefaultLockProperties.builder().build();
        }
    }
}
