package com.tanhua.autoconfig.template;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.properties.AipFaceProperties;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 19:30
 * 百度人脸识别
 */
public class ApiFaceTemplate {

    @Autowired
    private AipFace client;

    /**
     * 检测图片中的是否存在人脸
     *
     * @param imagePath 图片路径
     * @return true:存在人脸，false:不存在人脸
     */
    public boolean detectFace(String imagePath) {

        String imageType = "URL";
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");
        // 人脸检测
        JSONObject res = client.detect(imagePath, imageType, options);

        Object error_code = res.get("error_code");

        return (error_code != null && error_code.toString().equals("0"));
    }
}



