package com.tanhua.autoconfig.template;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.imageaudit20191230.Client;
import com.aliyun.imageaudit20191230.models.*;
import com.tanhua.autoconfig.properties.GreenProperties;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

import com.aliyun.teaopenapi.models.*;

import java.util.*;


/**
 * @Author: leah_ana
 * @Date: 2022/4/16 18:47
 * @Desc: 阿里巴巴内容审核
 */
@Slf4j
public class AlibabaGreenTemplate {

    private Client client;

    private GreenProperties greenProperties;

    public AlibabaGreenTemplate(GreenProperties properties) {

        try {
            this.greenProperties = properties;
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(properties.getAccessKeyID())
                    // 您的AccessKey Secret
                    .setAccessKeySecret(properties.getAccessKeySecret());
            // 访问的域名
            config.endpoint = greenProperties.getEndpoint();

            client = new Client(config);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("GreenTemplate初始化失败");
        }
    }

    public Map<String, String> imageScan(List<String> imageUrls) throws Exception {

        List<ScanImageRequest.ScanImageRequestTask> list = new ArrayList();
        if (imageUrls != null && imageUrls.size() != 0) {
            for (int i = 0; i < imageUrls.size(); i++) {
                if (imageUrls.get(i) != null) {
                    ScanImageRequest.ScanImageRequestTask task =
                            new ScanImageRequest.ScanImageRequestTask()
                                    .setImageURL(imageUrls.get(i))
                                    .setDataId(UUID.randomUUID().toString());
                    list.add(task);
                }
            }
        }
        String imageScanScenes = greenProperties.getImageScanScenes();
        String[] scenesStr = scenesStr(imageScanScenes);
        if (scenesStr.length == 0) {
            throw new Exception("scenesStr is null");
        }
        ScanImageRequest scanImageRequest = new ScanImageRequest()
                .setTask(list).setScene(java.util.Arrays.asList(
                        scenesStr));
        //  Results=[
        //  {DataId=97f2f209-958c-4688-9d78-c24569524186, TaskId=null, ImageURL=https://blob.keylol.com/forum/202204/16/170807aacpoxzo8i5ozhca.png?a=a,
        //  SubResults=[
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=99.9, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=porn, ProgramCodeDataList=null},
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=100.0, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=terrorism, ProgramCodeDataList=null},
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=99.91, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=ad, ProgramCodeDataList=null},
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=99.91, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=live, ProgramCodeDataList=null}]},
        //  {DataId=edc03b96-dfa3-439e-9525-31d019c52f59, TaskId=null, ImageURL=https://blob.keylol.com/forum/202204/16/170755tftpefdl4secedlf.png?a=a,
        //  SubResults=[
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=99.9, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=porn, ProgramCodeDataList=null},
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=100.0, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=terrorism, ProgramCodeDataList=null},
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=99.91, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=ad, ProgramCodeDataList=null},
        //  {Suggestion=pass, Frames=null, SfaceDataList=null, OCRDataList=null, Rate=99.91, HintWordsInfoList=null, Label=normal, LogoDataList=null, Scene=live, ProgramCodeDataList=null}]}]}}}

        // 复制代码运行请自行打印 API 的返回值
        HashMap<String, String> map = new HashMap<>();
        map.put("suggestion", "pass");
        ScanImageResponse scanImageResponse = client.scanImage(scanImageRequest);
        if (scanImageResponse != null
                && scanImageResponse.getBody() != null
                && scanImageResponse.getBody().getData() != null
                && scanImageResponse.getBody().getData().getResults() != null) {
            List<ScanImageResponseBody.ScanImageResponseBodyDataResults> results = scanImageResponse.getBody().getData().getResults();
            //results每张图片的校验结果
            loop:
            for (ScanImageResponseBody.ScanImageResponseBodyDataResults result : results) {
                //本张图片 每个校验标准的结果
                if (result.getSubResults() != null) {
                    List<ScanImageResponseBody.ScanImageResponseBodyDataResultsSubResults> subResults = result.getSubResults();
                    for (ScanImageResponseBody.ScanImageResponseBodyDataResultsSubResults subResult : subResults) {
                        if (subResult.getSuggestion() != null) {
                            System.out.println("图片校验结果：" + subResult.getSuggestion());
                            if (!"pass".equals(subResult.getSuggestion())) {
                                System.out.println("场景" + subResult.scene + "校验结果" + subResult.getSuggestion() + "图片校验未通过");
                                map.put("suggestion", subResult.getSuggestion());
                                break loop;
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    public Map<String, String> greenTextScan(String content) throws Exception {

        Map<String, String> resultMap = new HashMap();
        ScanTextRequest.ScanTextRequestTasks tasks = new ScanTextRequest.ScanTextRequestTasks()
                .setContent(content);
        String txtScanScenes = greenProperties.getTxtScanScenes();
        String[] scenesStr = scenesStr(txtScanScenes);
        System.err.println("scenesStr:" + Arrays.toString(scenesStr));
        if (scenesStr.length == 0) {
            throw new Exception("scenesStr is null");
        }
        List<ScanTextRequest.ScanTextRequestLabels> labelsList = new ArrayList<>();
        for (String scene : scenesStr) {
            ScanTextRequest.ScanTextRequestLabels labels = new ScanTextRequest.ScanTextRequestLabels()
                    .setLabel(scene);
            labelsList.add(labels);
        }

        ScanTextRequest scanTextRequest = new ScanTextRequest()
                .setLabels(labelsList)
                .setTasks(java.util.Arrays.asList(tasks));
        // 复制代码运行请自行打印 API 的返回值
        ScanTextResponse scanTextResponse = client.scanText(scanTextRequest);

        // {headers={access-control-allow-origin=*, date=Sat, 16 Apr 2022 15:34:31 GMT, content-length=184,
        // access-control-max-age=172800, x-acs-request-id=86EB94F3-C6AC-51A5-8733-3A5132151E11, access-control-allow-headers=X-Requested-With, X-Sequence, _aop_secret, _aop_signature, x-acs-action, x-acs-version, x-acs-date, Content-Type, connection=keep-alive, content-type=application/json;charset=utf-8, access-control-allow-methods=POST, GET, OPTIONS, PUT, DELETE, x-acs-trace-id=1fbbab5544a08357df8a8071ae88befb},
        // body=
        // {RequestId=86EB94F3-C6AC-51A5-8733-3A5132151E11, Data={Elements=[{TaskId=txt37lTGA3240Y59OXpSeIq7U-1w3c2X, Results=[{Suggestion=pass, Details=null, Rate=99.91, Label=normal}]}]}}}
//        Map<String, Object> map = scanTextResponse.toMap();
//        System.err.println(map);
        if (scanTextResponse != null
                && scanTextResponse.getBody() != null
                && scanTextResponse.getBody().getData() != null
                && scanTextResponse.getBody().getData().getElements() != null) {

            List<ScanTextResponseBody.ScanTextResponseBodyDataElements> elements = scanTextResponse.getBody().getData().getElements();
            for (ScanTextResponseBody.ScanTextResponseBodyDataElements element : elements) {
                List<ScanTextResponseBody.ScanTextResponseBodyDataElementsResults> results = element.getResults();
                if (results != null) {
                    for (ScanTextResponseBody.ScanTextResponseBodyDataElementsResults result : results) {
                        String suggestion = result.getSuggestion();
                        resultMap.put("suggestion", suggestion);
                        String label = result.getLabel();
                        if (suggestion.equals("review")) {
                            resultMap.put("reson", "文章内容中有不确定词汇");
                            log.info("返回结果，resultMap={}", resultMap);
                            return resultMap;
                        } else if (suggestion.equals("block")) {
                            String reson = "文章内容中有敏感词汇";
                            if (label.equals("spam")) {
                                reson = "文章内容中含垃圾信息";
                            } else if (label.equals("ad")) {
                                reson = "文章内容中含有广告";
                            } else if (label.equals("politics")) {
                                reson = "文章内容中含有涉政";
                            } else if (label.equals("terrorism")) {
                                reson = "文章内容中含有暴恐";
                            } else if (label.equals("abuse")) {
                                reson = "文章内容中含有辱骂";
                            } else if (label.equals("porn")) {
                                reson = "文章内容中含有色情";
                            } else if (label.equals("flood")) {
                                reson = "文章内容灌水";
                            } else if (label.equals("contraband")) {
                                reson = "文章内容违禁";
                            } else if (label.equals("meaningless")) {
                                reson = "文章内容无意义";
                            }
                            resultMap.put("reson", reson);
                            log.info("返回结果，resultMap={}", resultMap);
                            return resultMap;
                        }
                    }
                }
            }
        }
        resultMap.put("suggestion", "pass");
        resultMap.put("reson", "检测通过");
        return resultMap;
    }


    private String[] scenesStr(String scenes) {

        if (scenes != null) {
            return scenes.split(",");
        }
        return new String[]{""};
    }
}



