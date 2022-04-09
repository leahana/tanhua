package com.itheima.test;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 16:40
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class OssTest {

    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void testTemplateUpload() throws FileNotFoundException {

        String path = "D:\\桌面\\temp\\Mamami.jpg";
        FileInputStream is = new FileInputStream(path);

        String imageUrl = ossTemplate.upload(path, is);
        System.out.println("上传成功：" + imageUrl);
    }


    /**
     * 案例 上传文件到阿里云 OSS
     * 存放位置:/yyyy/MM/dd/xxxx.jpg
     *
     * @throws FileNotFoundException
     */
    @Test
    public void testOssUpload() throws FileNotFoundException {
        // 1.配置图片路径
        String path = "D:\\桌面\\temp\\Mamami.jpg";
        // 2.构造InputStream
        FileInputStream is = new FileInputStream(path);
        // 3.上传
        String filepath = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + UUID.randomUUID() + path.substring(path.lastIndexOf("."));


        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "xxxxxxxxxx.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "xxxxxxxxxxxxxxxxx";
        String accessKeySecret = "xxxxxxxxx";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "xxxx";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "exampledir/exampleobject.txt";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {

            // 创建PutObject请求。
            ossClient.putObject(bucketName, filepath, is);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
                String url = "https://leahana-tanhua.oss-cn-hangzhou.aliyuncs.com/" + filepath;
                System.out.println(url);
            }
        }
    }

}
