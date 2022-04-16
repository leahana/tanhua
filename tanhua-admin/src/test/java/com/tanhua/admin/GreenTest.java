package com.tanhua.admin;
/**
 * @Author: leah_ana
 * @Date: 2022/4/16 16:29
 */


import com.alibaba.fastjson.JSON;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.RpcAcsRequest;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.imageaudit.model.v20191230.ScanImageRequest;
import com.aliyuncs.imageaudit.model.v20191230.ScanImageResponse;
import com.aliyuncs.imageaudit.model.v20191230.ScanImageRequest.Task;

import com.aliyuncs.profile.DefaultProfile;
import com.netflix.client.ClientException;
import com.tanhua.autoconfig.template.AlibabaGreenTemplate;
import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GreenTest {
    @Autowired
    private AliyunGreenTemplate aliyunGreenTemplate;

    @Autowired
    private AlibabaGreenTemplate alibabaGreenTemplate;

    @Test
    public void greenTest() throws Exception {
        Map<String, String> map = aliyunGreenTemplate.greenTextScan("今天是个好日子");
        map.forEach((k, v) -> System.out.println(k + "==" + v));
    }


    static IAcsClient client = null;

    @Test
    public void testScanImage() throws Exception {


        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-x",
                "x",
                "x");

        client = new DefaultAcsClient(profile);

        System.out.println("--------  内容审核 --------------");
        ScanImageRequest req = new ScanImageRequest();

        List<String> scenes = new ArrayList<String>();
        scenes.add("porn");
        req.setScenes(scenes);
        List<Task> tasks = new ArrayList<Task>();
        com.aliyuncs.imageaudit.model.v20191230.ScanImageRequest.Task task = new Task();
        task.setDataId(UUID.randomUUID().toString());
        task.setImageURL("https://viapi-demo.oss-cn-shanghai.aliyuncs.com/viapi-demo/images/ChangeImageSize/change-image-size-src.png");
        tasks.add(task);
        req.setTasks(tasks);


        ScanImageResponse resp = getAcsResponse(req);
        printResponse(req.getSysActionName(), resp.getRequestId(), resp);
    }

    public static void printResponse(String actionName, String requestId, AcsResponse data) {
        System.out.println(String.format("actionName=%s, requestId=%s, data=%s", actionName, requestId,
                JSON.toJSONString(data)));
    }

    private static <R extends RpcAcsRequest<T>, T extends AcsResponse> T getAcsResponse(R req) throws Exception {
        try {
            return client.getAcsResponse(req);
        } catch (ServerException e) {
            // 服务端异常
            System.out.println(String.format("ServerException: errCode=%s, errMsg=%s", e.getErrCode(), e.getErrMsg()));
            throw e;
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testScanImage2() throws Exception {
        List<String> list = new ArrayList<>();

        list.add("https://blob.keylol.com/forum/202204/16/170807aacpoxzo8i5ozhca.png?a=a");
        list.add("https://blob.keylol.com/forum/202204/16/170755tftpefdl4secedlf.png?a=a");
        //alibabaGreenTemplate.print(list);
    }

    @Test
    public void test() throws Exception {
        Map<String, String> map = alibabaGreenTemplate.greenTextScan(
                "本校小额贷款，安全、快捷、方便、无抵押，随机随贷，当天放款，上门服务。联系weixin 123456");
        map.forEach((k, v) -> System.out.println(k + "==" + v));
    }

}