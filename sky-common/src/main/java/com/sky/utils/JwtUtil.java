package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成jwt (jjwt 0.13.0)
     * 使用 HS256 算法，私钥固定字符串
     *
     * @param secretKey jwt秘钥字符串
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return JWT字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 将字符串密钥转换为 SecretKey 对象（要求长度至少32字节，否则会抛出异常）
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttlMillis);

        return Jwts.builder()
                .claims(claims)               // 设置自定义声明
                .issuedAt(now)                // 签发时间
                .expiration(exp)              // 过期时间
                .signWith(key)                // 使用 SecretKey 签名
                .compact();
    }

    /**
     * Token解密 (jjwt 0.13.0)
     *
     * @param secretKey jwt秘钥字符串（必须与生成时一致）
     * @param token     加密后的token
     * @return Claims 对象
     * @throws Exception 解析失败（签名错误、过期等）抛出异常
     */
    public static Claims parseJWT(String secretKey, String token) throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)              // 设置验证密钥
                .build()                      // 构建解析器
                .parseSignedClaims(token)     // 解析签名JWT
                .getPayload();                // 获取负载（原 getBody）
    }
}