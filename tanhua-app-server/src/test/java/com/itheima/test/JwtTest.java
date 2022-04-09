package com.itheima.test;

import io.jsonwebtoken.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 13:06
 */
public class JwtTest {

    @Test
    public void testCreateToken() {
        //生成token
        // 1.准备数据
        Map map = new HashMap();
        map.put("id", "1");
        map.put("phone", "13333333333");
        // 2.jwt的工具类生成token
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "itcast")
                .setClaims(map)
                .setExpiration(new Date(now + 30000))
                .compact();
        System.out.println(token);

    }

    /**
     * SignatureException: token不合法
     * ExpiredJwtException: token过期
     */
    @Test
    public void testParserToken() {
        //解析token

        String token = "eyJhbGciOiJIUzUxMiJ9.eyJwaG9uZSI6IjEzMzMzMzMzMzMzIiwiaWQiOiIxIiwiZXhwIjoxNjQ5NDgxNjI5fQ.rdlnBPenA6UUh5lEpS6gae-5suxSXwd5HJnBVf2LYnj0D0mG1-MHHy_7YCbIQE3gtg7-vuZHRXp8WJAbefgUMQ";

        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey("itcast")
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("token过期");
        } catch (SignatureException e) {
            System.out.println("token不合法");
        }

        Object id = claims.get("id");
        Object phone = claims.get("phone");

        System.out.println(id + "==" + phone);

    }
}
