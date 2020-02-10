package com.mimose.component.jwt.starter.component;

import com.mimose.component.jwt.starter.confiuration.JwtStorage;
import com.mimose.component.jwt.starter.enums.AlgorithmEnum;
import com.mimose.component.jwt.starter.util.RSAUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * @Description jwt操作工具类
 * @Author ccy
 * @Date 2019/12/23
 */
@Slf4j
@AllArgsConstructor
public class JwtOperator {

    private JwtStorage jwtStorage;

    /**
     * 从token中获取claim
     *
     * @param token token
     * @return claim
     */
    public Claims getClaimsFromToken(String token) {
        try {
            Pair<Key, SignatureAlgorithm> ksPair = this.getKSPair(false);
            if(ObjectUtils.isEmpty(ksPair)){
                return null;
            }
            return Jwts.parser()
                    .setSigningKey(ksPair.getKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.error("token parsing error", e);
            throw new JwtException("Token invalided.");
        }
    }

    /**
     * 获取token的过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token)
                .getExpiration();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 已过期返回true，未过期返回false
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 计算token的过期时间
     *
     * @return 过期时间
     */
    public Date getExpirationTime() {
        return new Date(System.currentTimeMillis() + this.jwtStorage.getJwtProperties().getExpireTimeInSecond() * 1000);
    }

    /**
     * 为指定用户生成token
     *
     * @param claims 用户信息
     * @return token
     */
    public String generateToken(Map<String, Object> claims) throws Exception {
        Date createdTime = new Date();
        Date expirationTime = this.getExpirationTime();

        Pair<Key, SignatureAlgorithm> ksPair = this.getKSPair(true);
        if(ObjectUtils.isEmpty(ksPair)){
            return null;
        }
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(createdTime)
                .setExpiration(expirationTime)
                // 你也可以改用你喜欢的算法
                // 支持的算法详见：https://github.com/jwtk/jjwt#features
                .signWith(ksPair.getKey(), ksPair.getValue())
                .compact();
    }

    /**
     * 判断token是否非法
     *
     * @param token token
     * @return 未过期返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 获取加解密key和算法
     * @param encrypt 是否是加密
     * @return
     */
    private Pair<Key, SignatureAlgorithm> getKSPair(boolean encrypt) {
        try {
            String algorithm = this.jwtStorage.getJwtProperties().getAlgorithm();
            Key key;
            SignatureAlgorithm signatureAlgorithm;
            String secret = encrypt? this.jwtStorage.getJwtProperties().getEncryptSecret() : this.jwtStorage.getJwtProperties().getDecryptSecret();
            if(StringUtils.isEmpty(algorithm) || AlgorithmEnum.HS256.getKey().equals(algorithm)){
                byte[] keyBytes = jwtStorage.getJwtProperties().getEncryptSecret().getBytes();
                key = Keys.hmacShaKeyFor(keyBytes);
                signatureAlgorithm = AlgorithmEnum.HS256.getAlgorithm();
            }else if(AlgorithmEnum.RSA256.getKey().equals(algorithm)){
                key = RSAUtil.getPrivateKey(secret);
                signatureAlgorithm = AlgorithmEnum.RSA256.getAlgorithm();
            }else{
                return null;
            }
            return new Pair<>(key, signatureAlgorithm);
        } catch (Exception e) {
            log.error("getKSPair error", e);
            return null;
        }
    }
}
