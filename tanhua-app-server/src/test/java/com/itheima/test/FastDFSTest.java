package com.itheima.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.api.BlackListApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import com.tanhua.server.service.SettingsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 16:18
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class FastDFSTest {

    //用于文件上床与下载
    @Autowired
    private FastFileStorageClient client;

    //用户获取 Nginx访问路径
    @Autowired
    private FdfsWebServer fdfsWebServer;


    /**
     * 测试FastDFS
     */

    @Test
    public void testUpload() throws FileNotFoundException {
        // 1.指定文件
        File file = new File("D:\\桌面\\再见绘梨\\41.jpg");
        // 2.上传文件
        StorePath storePath = client.uploadFile(new FileInputStream(file), file.length(), "jpg", null);
        String fullPath = storePath.getFullPath();
        System.out.println(fullPath);
        // 3.拼接请求路径
        String url = fdfsWebServer.getWebServerUrl()+ fullPath;
        System.out.println(url);
    }


}
