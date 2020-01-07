package com.mimose.component.jwt.starter.confiuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description jwt参数仓库类
 * @Author ccy
 * @Date 2019/12/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtStorage implements Serializable {
    private static final long serialVersionUID = 1730480289829300312L;

    private JwtProperties jwtProperties;

}
