package com.tanhua.autoconfig.template;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.tanhua.autoconfig.properties.OssProperties;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 17:13
 * @Desc: 阿里云Oss文件上传
 */
public class OssTemplate {

    private final OssProperties properties;

    public OssTemplate(OssProperties properties) {
        this.properties = properties;
    }

    /**
     * 文件上传
     * @param fileName    文件名
     * @param inputStream 输入流
     */
    public String upload(String fileName, InputStream inputStream) {

        System.out.println(fileName);
        String filepath = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));

        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = properties.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = properties.getAccessKey();
        String accessKeySecret = properties.getSecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = properties.getBucketName();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String url= "";
        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, filepath, inputStream);
        } catch (Exception oe) {
            System.out.println("Error Message: " + oe.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
                url = properties.getUrl() + filepath;
            }
        }
        return url;
    }
}


