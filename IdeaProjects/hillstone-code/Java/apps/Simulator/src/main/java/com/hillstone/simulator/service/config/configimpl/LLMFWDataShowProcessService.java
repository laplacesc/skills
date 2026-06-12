package com.hillstone.simulator.service.config.configimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.ConfigConstant;
import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.service.config.ConfigProcessInterface;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;
import com.hillstone.simulator.utils.LLMUtils;
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
import java.util.List;

/**
 * 大模型向设备发送数据采集请求
 * cloud_llm-cli_show
 * 设备接收到对应xml数据后，使用数据通道上送结果
 *
 * @author cylv
 * @date 2024/7/9 16:03
 */
@Slf4j
@Service(ConfigConstant.LLM_DATA_SHOW_PROCESS_SERVICE)
public class LLMFWDataShowProcessService implements ConfigProcessInterface {

    @Autowired
    private DeviceInfoConfig deviceInfoConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void runConfigProcess(MessageObject mo) {
        log.info(mo.getXmlString());

        /**
         * <msg category="cloud_llm" type="cli_show" user="hillstone" source="LLM">
         *     <task_id>xxx</task_id>
         *     <cmds>
         *         <cmd>version</cmd>
         *         <cmd>logging event | include exhaust</cmd>
         *     </cmds>
         * </msg>
         */
        // 此处的标识ID其实是xml中的一个字段，并非是原来的标签中的taskid
        // 解析方法是通用的
        String markId = LLMUtils.getTaskId(mo.toString());
        String sn = deviceInfoConfig.getSn();

        uploadLLMData(sn, markId);
    }



    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel md) {
        log.info(mo.getXmlString());
        // 此处的标识ID其实是xml中的一个字段，并非是原来的标签中的taskid
        // 解析方法是通用的
        String markId = LLMUtils.getTaskId(mo.toString());
        String sn = deviceInfoConfig.getSn();
        List<String> cmdList = LLMUtils.extractCmdList(mo.getXmlString());
        uploadLLMData(sn, markId);

    }



    private void uploadLLMData(String sn, String markId) {
        try {
            log.info("uploadLLMData for device:{}, markId:{}", sn, markId);
            String url = deviceInfoConfig.getHttpUrl() + "1.0/data/file/cloud_llm/cli_show/" + sn + "/" + markId;

            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();


            form.add("data", generateFileSource("/demofile/llm/error.json", markId + ".txt"));
            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            try {
                String s = restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
                log.info("{} {}", url, s);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ByteArrayResource generateFileSource(String filePath, String fileName) throws IOException {
        InputStream ins = this.getClass().getResourceAsStream(filePath);

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
            throw e;
        }

        ByteArrayResource fileResource = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                // 设置文件名
                return fileName;
            }
        };
        return fileResource;
    }
}