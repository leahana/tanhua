package com.tanhua.admin.listener;

import cn.hutool.json.JSONGetter;
import com.alibaba.fastjson.JSON;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.domain.Log;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/16 15:16
 * @Desc: 日志监听器 用户操作日志记录
 */

@Component
public class LogListener {

    @Autowired
    private LogMapper logMapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "tanhua.log.queue",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = "tanhua.log.exchange",
                            type = ExchangeTypes.TOPIC),
                    key = "log.*"
            )
    )
    public void log(String message) {
        try {
            Map map = JSON.parseObject(message, Map.class);
            map.forEach((k, v) -> {
                System.err.println(k + "=====" + v);
            });
            // 1. 解析map
            Long userId = Long.valueOf(map.get("userId").toString());
            String type = (String) map.get("type");
            String logTime=(String) map.get("logTime");
            Log log = new Log(userId, logTime, type);
            // 2. 构造log对象存入数据库
            logMapper.insert(log);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
