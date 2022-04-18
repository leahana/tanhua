package com.tanhua.server.interceptor;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 22:23
 * @Desc: 自定义拦截器 统一验证token(加入网关之后由网关统一验证)
 */

public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头中的token
        String token = request.getHeader("Authorization");

        //2.实用工具类判断token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//
//        //3. token失效返回401 拦截
//        if (!verifyToken) {
//            response.setStatus(401);
//            return false;
//        }
        //4. token合法返回200 放行

        //解析token,获取id和手机号,构造User对象存入ThreadLocal
        Claims claims = JwtUtils.getClaims(token);
        //获取id
        Integer id = (Integer) claims.get("id");
        //获取手机号
        String mobile = (String) claims.get("mobile");

        //构造User对象
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setMobile(mobile);

        UserHolderUtil.setThreadLocal(user);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清除ThreadLocal
        UserHolderUtil.remove();
    }
}
