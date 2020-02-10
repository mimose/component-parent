package com.mimose.component.jwt.starter.aop;

import com.mimose.component.jwt.starter.annotation.MLogin;
import com.mimose.component.jwt.starter.component.JwtOperator;
import com.mimose.component.jwt.starter.exception.JwtCheckException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Description 权限校验切面
 * @Author ccy
 * @Date 2020/1/3
 */
//@Aspect
//@Component
@Deprecated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MLoginAspect {

    private final JwtOperator jwtOperator;

    @Around("@annotation(mLogin)")
    public Object check(ProceedingJoinPoint proceedingJoinPoint, MLogin mLogin) throws Throwable {
        // 判断是否登录
        this.checkLogin();
        // 判断权限
        this.checkRole(Arrays.asList(mLogin.role()));
        return proceedingJoinPoint.proceed();
    }

    private void checkLogin() {
        try {
            // 1. 从header里面获取token
            HttpServletRequest request = this.getHttpServletRequest();
            String token = request.getHeader("X-Token");
            // 2. 校验token是否合法&是否过期；如果不合法或已过期直接抛异常；如果合法放行
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid) {
                throw new SecurityException("Token不合法！");
            }
            // 3. 如果校验成功，那么就将用户的信息设置到request的attribute里面
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("userInfo", claims);
        } catch (Exception e) {
            throw new JwtCheckException("Token不合法");
        }
    }

    private void checkRole(List<String> roles) {
        try {
            if(CollectionUtils.isEmpty(roles)){
                return;
            }
            HttpServletRequest request = this.getHttpServletRequest();
            Claims claims = (Claims) request.getAttribute("userInfo");
            if(claims == null){
                throw new SecurityException("没有登录信息");
            }
            List<String> userRoles = claims.containsKey("role")? claims.get("role", List.class) : null;
            if(CollectionUtils.isEmpty(userRoles) || !Collections.disjoint(roles , userRoles)){
                throw new JwtCheckException("没有权限");
            }
        } catch (Exception e) {
            throw new JwtCheckException("没有权限");
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getRequest();
    }
}
