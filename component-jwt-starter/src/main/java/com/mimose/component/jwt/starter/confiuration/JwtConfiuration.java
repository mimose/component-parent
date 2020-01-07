package com.mimose.component.jwt.starter.confiuration;

import com.mimose.component.jwt.starter.component.JwtOperator;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description jwt配置类
 * @Author ccy
 * @Date 2019/12/23
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiuration {

    /**
     * 实例化仓库类
     * @return
     */
    @Bean
    public JwtStorage jwtStorage(JwtProperties jwtProperties){
        return JwtStorage.builder().jwtProperties(jwtProperties).build();
    }

    /**
     * 实例化操作类
     * @param jwtStorage
     * @return
     */
    @Bean
    public JwtOperator jwtOperator(JwtStorage jwtStorage){
        return new JwtOperator(jwtStorage);
    }

}
