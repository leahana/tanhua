package com.tanhua.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.commons.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/15 20:06
 * @Desc: 网关过滤器 统一token验证
 */

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${gateway.excludedUrls}")
    private List<String> excludedUrls;

    //过滤器核心业务代码
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        excludedUrls.forEach(System.out::println);
        // 1.  排除不需要权限校验的链接
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("path:" + path);
        if (excludedUrls.contains(path)) {
            return chain.filter(exchange);
        }
        // 2. 获取token链接
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.err.println(token);
        if (!StringUtils.isEmpty(token)) {
            //获取token
            token = token.replaceAll("Bearer ", "");
        }
        System.err.println(token);
        //2、使用工具类，判断token是否有效
        boolean verifyToken = JwtUtils.verifyToken(token);
        //3、如果token失效，返回状态码401，拦截
        System.err.println(verifyToken);
        if (!verifyToken) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("errCode", 401);
            responseData.put("errMessage", "用户未登录");
            return responseError(exchange.getResponse(), responseData);
        }
        return chain.filter(exchange);
    }

    //响应错误数据
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {
        // 将信息转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 输出错误信息到页面
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    //配置执行顺序
    @Override
    public int getOrder() {
        return 0;
    }
}
