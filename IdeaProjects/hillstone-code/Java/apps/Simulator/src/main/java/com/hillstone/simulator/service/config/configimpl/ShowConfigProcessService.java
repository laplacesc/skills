package com.hillstone.simulator.service.config.configimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: bohuachen
 * @date: 2023/6/25 9:51
 * @description: 巡检
 */
@Slf4j
@Service("showConfigProcessService")
public class ShowConfigProcessService implements ConfigProcessInterface {


    @Autowired
    private DeviceInfoConfig deviceInfoConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void runConfigProcess(MessageObject mo) {
        log.info(mo.getXmlString());
        log.info("执行show命令");
        String id = getId(mo);
        String version = id.substring(0, 2);
        if (version.equals("dl")) {
            ArrayList<String> cmds = getCmds(mo);
            try {
                sendShowResult(id, cmds, deviceInfoConfig.getSn());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (version.equalsIgnoreCase("ci")) {
            log.info("巡检使用");
            ArrayList<String> cmds = getCmds(mo);
            try {
                sendShowResult(id, cmds, deviceInfoConfig.getSn());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getId(MessageObject mo) {
        String id = "";
        Pattern idPattern = Pattern.compile("<id>(.+)</id>");
        Matcher matcherId = idPattern.matcher(mo.toString());
        if (matcherId.find()) {
            id = matcherId.group(1);
        }
        return id;
    }

    private ArrayList<String> getCmds(MessageObject mo) {
        Pattern cmdPattern = Pattern.compile("<cmd>(.+)</cmd>");
        ArrayList<String> cmds = new ArrayList<>();
        Matcher matcherCmd = cmdPattern.matcher(mo.toString());
        while (matcherCmd.find()) {
            cmds.add(matcherCmd.group(1));
        }
        return cmds;
    }


    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel multiDeviceConfigModel) {
        log.debug(mo.getXmlString());
        log.debug("设备:{} 执行show命令", multiDeviceConfigModel.getSn());
        String id = getId(mo);
        String version = id.substring(0, 2);
        if (version.equals("dl")) {
            ArrayList<String> cmds = getCmds(mo);
            try {
                sendShowResult(id, cmds, multiDeviceConfigModel.getSn());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (version.equalsIgnoreCase("ci")) {
            log.info("巡检使用");
            ArrayList<String> cmds = getCmds(mo);
            try {
                sendShowResult(id, cmds, multiDeviceConfigModel.getSn());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送show的结果
     *
     * @param id
     * @param cmdList
     * @throws IOException
     */
    private void sendShowResult(String id, ArrayList<String> cmdList, String sn) throws IOException {
        String url = deviceInfoConfig.getHttpUrl() + "/1.0/data/file/Inspection/" + id + "/" + sn;
        StringBuilder showResult = new StringBuilder("show result of");
        for (String cmd : cmdList) {

            //读取show命令，show命令的内容就是文件名
            //由于windows不支持 特殊字符 如果出现特殊处理，此处用_替换了|
            String fileRoad = "/inspection_check_file/"+cmd.replaceAll("\\|","_").replaceAll("/","_")+".txt";

            InputStream ins = this.getClass()
                    .getResourceAsStream(fileRoad);
            log.info(fileRoad);

            //文件很可能找不到，比如具体接口，当前仅适配了接口0/0
            if(ins!=null){
                showResult.append(getSupportShow());
                showResult.append(cmd);
                showResult.append("\n");
                InputStreamReader in =  new InputStreamReader(ins,"UTF-8");
                try (BufferedReader br = new BufferedReader(in)) {
                    StringBuilder content = new StringBuilder();
                    String showResultContent;
                    while ((showResultContent = br.readLine()) != null) {
                        content.append(showResultContent);
                        content.append("\n");
                    }
                    showResult.append(content);
                }
            }else {
                showResult.append(getNotSupportShow());
                //理论上没法支持多条命令并发
                log.error("当前模拟器不支持此show命令，请在对应目录下添加文件");
            }

        }
        //设置请求体，注意是LinkedMultiValueMap
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("data", new ByteArrayResource(showResult.toString().getBytes()) {
            @Override
            public String getFilename() {
                return cn.hutool.core.lang.UUID.randomUUID().toString();
            }
        });
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        try {
            log.info(url + " " );
            String s = restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
            log.info(url + " " + s);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public String getNotSupportShow(){
        return "<cmds code=\"0\">\n";
    }
    public String getSupportShow(){
        return "<cmds code=\"1\">\n";
    }

}
