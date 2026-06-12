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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bhliu
 * @date create in 18:19 2024/01/11
 * @description
 */
@Slf4j
@Service(ConfigConstant.IPS_SIGNATURE_UPGRADE_NAME)
public class IpsSignatureUpgradeConfigProcessService implements ConfigProcessInterface {
    @Autowired
    private DeviceInfoConfig deviceInfoConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void runConfigProcess(MessageObject mo) {
        log.info(mo.getXmlString());
        log.info("执行show命令");
        String result = getSuccessResult(mo.getType(), mo);
        String taskId = getId(mo);
        String messageId = getMessageId(mo);
        String replace = result.replace("--", messageId);
        try {
            sendResult(taskId, deviceInfoConfig.getSn(), mo.getType(), replace);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel md) {
        log.debug(mo.getXmlString());
        log.debug("设备:{} 执行show命令",md.getSn());
        String result = getSuccessResult(mo.getType(), mo);
        String taskId = getId(mo);
        String messageId = getMessageId(mo);
        String replace = result.replace("--", messageId);
        try {
            sendResult(taskId, md.getSn(), mo.getType(), replace);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String getSuccessResult(String type, MessageObject mo){
        if (type.equals(ConfigConstant.CLOUD_SIGNATURE_UPGRADE_TYPE)) {
            String result = "<msg category=\"cloud_platform_configuration\"><ok-info><vsys id=\"0\"><msg-sub-id>--</msg-sub-id><node-path>/execution/signature/import</node-path></vsys></ok-info><rpc-reply message-id=\"--\"><ok/></rpc-reply></msg>";
            return result;
        }
        if (type.equals(ConfigConstant.CLOUD_SIGNATURE_QUERY_TYPE)) {
            String result = "<msg category=\"cloud_platform_configuration\"><rpc-reply message-id=\"--\"><get-config><configuration start-index=\"0\" end-index=\"199\" total-num=\"1\" revision-num=\"0\"><vsys id=\"0\"><updated>\n<job>\n<index>0</index>\n<name>IPS</name>\n<schd_type>0</schd_type>\n<schd_monthday>24</schd_monthday>\n<schd_weekday>4</schd_weekday>\n<schd_hour>2</schd_hour>\n<schd_minute>14</schd_minute>\n<mode>0</mode>\n<stat_report>0</stat_report>\n<http_proxy_main_port>0</http_proxy_main_port>\n<http_proxy_backup_port>0</http_proxy_backup_port>\n<max_download_time>0</max_download_time>\n<protocol>1</protocol>\n<server1>update1.hillstonenet.com</server1>\n<server2>update2.hillstonenet.com</server2>\n<port1>443</port1>\n<port2>443</port2>\n<port3>443</port3>\n<default_server1>update1.hillstonenet.com</default_server1>\n<default_server2>update2.hillstonenet.com</default_server2>\n<vrouter1>trust-vr</vrouter1>\n<vrouter2>trust-vr</vrouter2>\n<vrouter3>trust-vr</vrouter3>\n</msg><msg category=\"cloud_platform_configuration\"><action>0</action>\n<status>0</status>\n<magic>e58ff09817ccfd533823f7f180a4d41fa82b</magic>\n<last_update_result>3</last_update_result>\n<last_update_time>1705568383</last_update_time>\n<last_sync_result>0</last_sync_result>\n<release_date>2024/01/15</release_date>\n<version>3.0.191</version>\n<latest_version>3.0.19</latest_version>\n<license_expire>2024-03-15</license_expire>\n<auto_update>0</auto_update>\n<nbc_update_info>最新的特征库，无须升级</nbc_update_info>\n<sig_eng_ver>1</sig_eng_ver>\n<av_sig_count>0</av_sig_count>\n<ptf_sig_count>0</ptf_sig_count>\n<ips_pcre>1</ips_pcre>\n<ips_full>0</ips_full>\n<ip_rep_full>0</ip_rep_full>\n</job>\n</msg><msg category=\"cloud_platform_configuration\"></updated>\n</vsys></configuration></get-config></rpc-reply></msg>";
            return result;
        }
        return null;
    }


    private void sendResult(String taskId, String sn, String type, String result) throws IOException {
        String url = deviceInfoConfig.getHttpUrl() + "1.0/data/file/cloud_platform_configuration/" + type + "/" + sn + "/" + taskId;
        //设置请求体，注意是LinkedMultiValueMap
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("data", new ByteArrayResource(result.getBytes()) {
            @Override
            public String getFilename() {
                return cn.hutool.core.lang.UUID.randomUUID().toString();
            }
        });
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

    private static String getId(MessageObject mo) {
        Pattern idPattern = Pattern.compile("task_id=\"([^>\"]+)\">");
        Matcher matcherId = idPattern.matcher(mo.toString());
        if (matcherId.find()) {
            return matcherId.group(1);
        }
        return null;
    }

    private static String getMessageId(MessageObject mo) {
        Pattern idPattern = Pattern.compile("rpc message-id=\"([^\"]+)\"");
        Matcher matcherId = idPattern.matcher(mo.toString());
        if (matcherId.find()) {
            return matcherId.group(1);
        }
        return null;
    }
}
