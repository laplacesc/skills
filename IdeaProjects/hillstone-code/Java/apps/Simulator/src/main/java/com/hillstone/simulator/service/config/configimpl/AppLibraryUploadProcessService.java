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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xjhuang
 * @date create in 14:59 2024/3/17
 * @description
 */
@Slf4j
@Service(ConfigConstant.APP_LIBRARY_UPLOAD_CONFIG_PROCESS_SERVICE)
public class AppLibraryUploadProcessService implements ConfigProcessInterface {

    @Autowired
    private DeviceInfoConfig deviceInfoConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void runConfigProcess(MessageObject mo) {
        log.info(mo.getXmlString());
        String taskId = getId(mo);
        String sn = deviceInfoConfig.getSn();

        uploadAppLibraryFile(sn, taskId);
    }

    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel md) {
        log.info(mo.getXmlString());
    }

    private static String getId(MessageObject mo) {
        Pattern idPattern = Pattern.compile("task_id=\"([^>\"]+)\">");
        Matcher matcherId = idPattern.matcher(mo.toString());
        if (matcherId.find()) {
            return matcherId.group(1);
        }
        return null;
    }

    private void uploadAppLibraryFile(String sn, String taskId){
        try {
            log.info("uploadAppLibraryFile for device:{}, with taskId:{}", sn, taskId);
            String url = deviceInfoConfig.getHttpUrl() + "1.0/data/file/cloud_platform_configuration/" + ConfigConstant.SYNC_APP_LIBRARY + "/" + sn;


            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();

            InputStream ins = this.getClass()
                    .getResourceAsStream("/app/pre_servgroup_desc.xml.gz");

            byte[] data;
            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                int nRead;
                byte[] byteBuffer = new byte[16384];
                while ((nRead = ins.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    buffer.write(byteBuffer, 0, nRead);
                }
                buffer.flush();
                data = buffer.toByteArray();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return;
            }

            ByteArrayResource fileResource = new ByteArrayResource(data) {
                @Override
                public String getFilename() {
                    return "pre_servgroup_desc.xml.gz"; // 设置文件名
                }
            };


            form.add("data", fileResource);
            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            try {
                String s = restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
                log.info(url + " " + s);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

    }
}
