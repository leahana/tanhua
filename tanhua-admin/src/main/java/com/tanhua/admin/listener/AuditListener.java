package com.tanhua.admin.listener;

import com.tanhua.api.MovementApi;
import com.tanhua.autoconfig.template.AlibabaGreenTemplate;
import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboReference;
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
 * @Date: 2022/4/16 19:48
 */

/**
 * RabbitMq监听器
 */
@Component
public class AuditListener {


    @DubboReference
    private MovementApi movementApi;

//    @Autowired //这个阿里云审核的template暂时用不来 要开通 我没有钱!
//    private AliyunGreenTemplate aliyunGreenTemplate;


    @Autowired
    private AlibabaGreenTemplate template;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "tanhua.audit.queue",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = "tanhua.audit.exchange",
                            type = ExchangeTypes.TOPIC
                    ),
                    key = "audit.movement"
            )
    )
    public void audit(String movementId) {
        try {
            // 1.根据id查询动态
            Movement movement = movementApi.findByMomentId(movementId);
            // 2.审核文本,审核图片
            if (movement != null && movement.getState() == 0) {
                Map<String, String> txtScan = template.greenTextScan(movement.getTextContent());
                Map imagScan = template.imageScan(movement.getMedias());
                int state = 0;
                if (txtScan != null && imagScan != null) {
                    String textSuggestion = txtScan.get("suggestion");
                    String imageSuggestion = (String) imagScan.get("suggestion");
                    if ("block".equals(textSuggestion) || "block".equals(imageSuggestion)) {
                        state = 2;
                    } else if ("pass".equals(textSuggestion) && "pass".equals(imageSuggestion)) {
                        state = 1;
                    }

                }
                // 3.判断审核结果

            movementApi.updateState(movementId, state);

            }
            // 4.更新动态状态
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
