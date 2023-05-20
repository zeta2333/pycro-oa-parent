package pycro.usts.common.jwt;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-20 11:12 AM
 * jwt工具类
 */
public class JwtHelper {

    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;
    private static String tokenSignKey = "123456";

    /**
     * 根据用户id和用户名称生成token字符串
     * @param userId
     * @param username
     * @return
     */
    public static String createToken(Long userId, String username) {
        String token = Jwts.builder()
                // 设置分类
                .setSubject("AUTH-USER")
                // 设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                // 设置主体信息
                .claim("userId", userId)
                .claim("username", username)
                // 设置签名
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    /**
     * 从生成的token字符串获取用户id
     * @param token
     * @return
     */
    public static Long getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) return null;

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从生成的token字符串获取用户名称
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(322L, "Kight");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUsername(token));
    }
}