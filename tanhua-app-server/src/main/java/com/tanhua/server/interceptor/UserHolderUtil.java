package com.tanhua.server.interceptor;

import com.tanhua.model.domain.User;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 22:40
 * @Desc: 工具类: 向ThreadLocal中存储用户信息
 */

public class UserHolderUtil {

    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    //将用户对象 存入ThreadLocal
    public static void setThreadLocal(User user) {
        threadLocal.set(user);
    }

    //从ThreadLocal中获取用户对象
    public static User getThreadLocal() {
        return threadLocal.get();
    }

    //从当前线程,获取用户对象的id
    public static Long getUserId() {
        return threadLocal.get().getId();
    }

    //从当前线程,获取用户对象的手机
    public static String getMobile() {
        return threadLocal.get().getMobile();
    }

    //更新用户手机
    public static void setMobile(String mobile) {
        threadLocal.get().setMobile(mobile);
    }

    //清空线程
    public static void remove() {
        threadLocal.remove();
    }
}
