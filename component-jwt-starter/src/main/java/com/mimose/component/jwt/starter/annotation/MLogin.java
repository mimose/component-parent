package com.mimose.component.jwt.starter.annotation;

import java.lang.annotation.*;

/**
 * @Description 是否登录、是否拥有权限
 * @Author ccy
 * @Date 2020/1/3
 */
@Deprecated
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MLogin {

    // 角色
    String[] role();

}
