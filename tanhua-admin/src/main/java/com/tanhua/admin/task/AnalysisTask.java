package com.tanhua.admin.task;

import com.tanhua.admin.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: leah_ana
 * @Date: 2022/4/16 15:56
 * @Desc: 定时更新数据
 */

@Component
public class AnalysisTask {


    @Autowired
    private AnalysisService analysisService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void analysis() {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.err.println("当前时间" + time);
        try{
            analysisService.analysis();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
