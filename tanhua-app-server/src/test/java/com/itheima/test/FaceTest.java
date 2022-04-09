package com.itheima.test;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.template.ApiFaceTemplate;
import com.tanhua.server.AppServerApplication;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 19:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class FaceTest {

    @Autowired
    private ApiFaceTemplate apiFaceTemplate;

    @Test
    public void testDetect(){
        String image = "https://pic4.zhimg.com/v2-7b5792f1cd1a3c27a732a1b1c5ffaa32_720w.jpg?source=172ae18b";
        boolean flag = apiFaceTemplate.detectFace(image);
        System.out.println(flag);
    }

        //设置APPID/AK/SK
        public static final String APP_ID = "xxxx";
        public static final String API_KEY = "xxxxxxxxx";
        public static final String SECRET_KEY = "xxxxxxxxxxx";

        public static void main(String[] args) {
            // 初始化一个AipFace
            AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);


            // 调用接口
            String image = "https://pic4.zhimg.com/v2-7b5792f1cd1a3c27a732a1b1c5ffaa32_720w.jpg?source=172ae18b";
            String imageType = "URL";

            HashMap<String, String> options = new HashMap<String, String>();
            options.put("face_field", "age");
            options.put("max_face_num", "2");
            options.put("face_type", "LIVE");
            options.put("liveness_control", "LOW");

            // 人脸检测
            JSONObject res = client.detect(image, imageType, options);
            Object error_code = res.get("error_code");

            System.out.println(res.toString(2));

        }

}
