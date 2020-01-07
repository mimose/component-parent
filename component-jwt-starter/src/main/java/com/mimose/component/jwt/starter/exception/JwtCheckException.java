package com.mimose.component.jwt.starter.exception;

/**
 * @Description jwt 判断异常
 * @Author ccy
 * @Date 2020/1/3
 */
public class JwtCheckException extends SecurityException {

    public JwtCheckException(String message) {
        super(message);
    }
}
