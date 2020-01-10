package com.mimose.component.jwt.starter.confiuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @Description jwt属性类
 * @Author ccy
 * @Date 2019/12/23
 */
@Data
@ConfigurationProperties(prefix = "m.jwt")
public class JwtProperties implements Serializable {

    private static final long serialVersionUID = -6236397139503897945L;

    /**
     * 加密密钥
     */
    private String encryptSecret;

    /**
     * 解密密钥
     */
    private String decryptSecret;

    /**
     * 过期时间
     */
    private Long expireTimeInSecond;

    /**
     * 使用哪种加密算法，默认使用HS256
     */
    private String algorithm;
}
