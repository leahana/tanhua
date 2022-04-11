package com.itheima.test;

import com.tanhua.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class MovementApiTest {

    @DubboReference
    private MovementApi movementApi;

    @Test
    public void testPublish() {
        Movement movement = new Movement();
        movement.setUserId(106l);
        movement.setTextContent("梦里不觉秋已深,余情岂是为他人");
        List<String> list = new ArrayList<>();
        list.add("https://leahana-tanhua.oss-cn-hangzhou.aliyuncs.com/2022/04/09/f9e4f027-164d-4459-b980-0efdb59d4206.jpg");
        list.add("https://leahana-tanhua.oss-cn-hangzhou.aliyuncs.com/2022/04/09/c17a7aeb-f84c-4068-9e7a-7468e7df7a8f.jpg");
        movement.setMedias(list);
        movement.setLatitude("40.066355");
        movement.setLongitude("116.350426");
        movement.setLocationName("中国北京市昌平区建材城西路16号");
        movementApi.publishMovement(movement);
    }
}