package com.hillstone.simulator.service.config.configimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.ConfigConstant;
import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.service.config.ConfigProcessInterface;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

/**
 * @author myyang
 * @date create in 14:45 2024/03/20
 * @description
 */
@Slf4j
@Service(ConfigConstant.BLOCK_CONFIG_PROCESS_NAME)
public class BlockConfigProcessService implements ConfigProcessInterface {

    @Autowired
    private DeviceInfoConfig deviceInfoConfig;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public void runConfigProcess(MessageObject mo) {
        log.info(mo.getXmlString());
        String taskId = getTaskId(mo);
        List<String> subMsgIds = getMsgSubId(mo);
        String[] types = mo.getType().split(":");
        String result = "";
        switch (types[1]) {
            case "ip":
                result = generateIpBlockResult(subMsgIds);
                break;
            case "url":
                result = generateUrlBlockResult(subMsgIds);
                break;
            case "domain":
                result = generateDomainBlockResult(subMsgIds);
                break;
            default:
                log.error("type error mo :{}", mo.toString());
        }
        try {
          sleep(2000);
          sendResult(taskId, deviceInfoConfig.getSn(), mo.getType(), result);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel md) {
        log.info(mo.getXmlString());
        String taskId = getTaskId(mo);
        List<String> subMsgIds = getMsgSubId(mo);
        String[] types = mo.getType().split(":");
        String result = "";
        switch (types[1]) {
            case "ip":
                result = generateIpBlockResult(subMsgIds);
                break;
            case "url":
                result = generateUrlBlockResult(subMsgIds);
                break;
            case "domain":
                result = generateDomainBlockResult(subMsgIds);
                break;
            default:
                log.error("type error mo :{}", mo.toString());
        }
        try {
            sleep(2000);
            sendResult(taskId, md.getSn(), mo.getType(), result);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateDomainBlockResult(List<String> subMsgIds) {
        StringBuilder result = new StringBuilder("<msg category=\"cloud_platform_configuration\">");
        for (String subMsgId : subMsgIds) {
            //生成ok-info
            result.append(generateDomainBlockOkResult(subMsgId));
            //生成error-info
//            result.append(generateDomainBlockErrorResult(subMsgId));
        }
        result.append("</msg>");
        return result.toString();
    }

    private String generateDomainBlockOkResult(String subMsgId) {
        StringBuilder result = new StringBuilder("<ok-info>\n" +
                "<vsys id=\"0\">\n" +
                "<msg-sub-id>");
        result.append(subMsgId);
        result.append("</msg-sub-id>\n" +
                "<node-path>/configuration/botnetdetection/botnet_black</node-path>\n" +
                "<msg>\n" +
                "<botnetdetection>\n" +
                "<botnet_black>\n" +
                //实体不会实时变化，固定的但不会影响业务解析
                "<entry>https://asdw23.3123</entry>\n" +
                "<port>0</port>\n" +
                //带子域名的为3，不带的为2，此处固定为2，但不会影响业务解析
                "<type>2</type>\n" +
                "</botnet_black>\n" +
                "</botnetdetection>\n" +
                "</msg>\n" +
                "</vsys>\n" +
                "</ok-info>");
        return result.toString();
    }

    private String generateDomainBlockErrorResult(String subMsgIds) {
        StringBuilder result = new StringBuilder("<error-info>\n" +
                "<vsys id=\"0\">\n" +
                "<msg-sub-id>");
        result.append(subMsgIds);
        result.append("</msg-sub-id>\n" +
                "<node-path>/configuration/botnetdetection/botnet_black</node-path>\n" +
                "<error-code>400000002</error-code>\n" +
                "<msg>错误：添加僵尸网络特征条目失败，该条目已存在</msg>\n" +
                "</vsys>\n" +
                "</error-info>");
        return result.toString();
    }


    private String generateUrlBlockResult(List<String> subMsgIds) {
        StringBuilder result = new StringBuilder("<msg category=\"cloud_platform_configuration\">");
        for (String subMsgId : subMsgIds) {
            //生成ok-info
            result.append(generateUrlBlockOkResult(subMsgId));
            //生成error-info
//            result.append(generateUrlBlockErrorResult(subMsgId));
        }
        result.append("</msg>");
        return result.toString();
    }

    private String generateUrlBlockOkResult(String subMsgIds) {
        StringBuilder result = new StringBuilder("<ok-info>\n" +
                "<vsys id=\"0\">\n" +
                "<msg-sub-id>");
        result.append(subMsgIds);
        result.append("</msg-sub-id>\n" +
                "<node-path>/configuration/botnetdetection/botnet_black</node-path>\n" +
                "<msg>\n" +
                "<botnetdetection>\n" +
                "<botnet_black>\n" +
                //实体不会实时变化，固定的但不会影响业务解析
                "<entry>https://asdw23.3123</entry>\n" +
                "<port>0</port>\n" +
                "<type>5</type>\n" +
                "</botnet_black>\n" +
                "</botnetdetection>\n" +
                "</msg>\n" +
                "</vsys>\n" +
                "</ok-info>");
        return result.toString();
    }

    private String generateUrlBlockErrorResult(String subMsgIds) {
        StringBuilder result = new StringBuilder("<error-info>\n" +
                "<vsys id=\"0\">\n" +
                "<msg-sub-id>");
        result.append(subMsgIds);
        result.append("</msg-sub-id>\n" +
                "<node-path>/configuration/botnetdetection/botnet_black</node-path>\n" +
                "<error-code>400000002</error-code>\n" +
                "<msg>错误：添加僵尸网络特征条目失败，该条目已存在</msg>\n" +
                "</vsys>\n" +
                "</error-info>");
        return result.toString();
    }



    private String generateIpBlockResult(List<String> subMsgIds) {
        StringBuilder result = new StringBuilder("<msg category=\"cloud_platform_configuration\">\n");
        for (String subMsgId : subMsgIds) {
            //生成ok-info
            result.append(generateIpBlockOkResult(subMsgId));
            //生成error-info
//            result.append(generateIpBlockErrorResult(subMsgId));
        }
        result.append("</msg>");
        return result.toString();

    }

    private String generateIpBlockOkResult(String subMsgId) {
        StringBuilder result = new StringBuilder("<ok-info>\n" +
                "<vsys id=\"0\">\n" +
                "<msg-sub-id>");
        result.append(subMsgId);
        result.append("</msg-sub-id>\n" +
                "<node-path>/configuration/behavior/blockIp</node-path>\n" +
                "</vsys>\n" +
                "</ok-info>");
        return result.toString();
    }

    private String generateIpBlockErrorResult(String subMsgId) {
        StringBuilder result = new StringBuilder("<error-info>\n" +
                "<vsys id=\"0\">\n" +
                "<msg-sub-id>");
        result.append(subMsgId);
        result.append("</msg-sub-id>\n" +
                "<node-path>/configuration/behavior/blockIp</node-path>\n" +
                "<error-code>400000002</error-code>\n" +
                "<msg>错误：阻断IP已经被创建\\n</msg>\n" +
                "</vsys>\n" +
                "</error-info>");
        return result.toString();
    }




    private void sendResult(String taskId, String sn, String type, String result) {
        //设置请求体，注意是LinkedMultiValueMap
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("data", new ByteArrayResource(result.getBytes()) {
            @Override
            public String getFilename() {
                return cn.hutool.core.lang.UUID.randomUUID().toString();
            }
        });
        String url = deviceInfoConfig.getHttpUrl() + "1.0/data/file/cloud_platform_configuration/" + type + "/" + sn + "/" + taskId;
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            String s = restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
            log.info(url + " " + s);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String getTaskId(MessageObject mo) {
        Pattern idPattern = Pattern.compile("task_id=\"([^>\"]+)\">");
        Matcher matcherId = idPattern.matcher(mo.toString());
        if (matcherId.find()) {
            return matcherId.group(1);
        }
        return null;
    }

    private static List<String> getMsgSubId(MessageObject mo) {
        List<String> list = new ArrayList<>();
        Pattern idPattern = Pattern.compile("msg-sub-id=\"(\\d+?)\"");
        Matcher matcherId = idPattern.matcher(mo.toString());
        while(matcherId.find()) {
            list.add(matcherId.group(1));
        }
        return list;
    }

    private static List<String> getEntry(MessageObject mo) {
        List<String> list = new ArrayList<>();
        Pattern idPattern = Pattern.compile("<entry>(.+?)<\\/entry>");
        Matcher matcherId = idPattern.matcher(mo.toString());
        while(matcherId.find()) {
            list.add(matcherId.group(1));
        }
        return list;
    }





}
