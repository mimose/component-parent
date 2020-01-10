package com.mimose.component.jwt.starter.enums;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

/**
 * @Description 加解密算法枚举
 * @Author ccy
 * @Date 2020/1/8
 */
@Getter
public enum AlgorithmEnum {

    HS256("HS256", SignatureAlgorithm.HS256),

    RSA256("RSA256", SignatureAlgorithm.RS256);

    private String key;

    private SignatureAlgorithm algorithm;

    AlgorithmEnum(String key, SignatureAlgorithm algorithm) {
        this.key = key;
        this.algorithm = algorithm;
    }
}
