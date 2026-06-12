package com.hillstone.simulator.service;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.entity.qo.BotChatS1Qo;
import com.hillstone.simulator.entity.qo.ChatMsgQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author bohuachen
 * @date 2025/3/19 13:50
 * @description
 */
@Slf4j
@Service
public class DeviceService {

    @Autowired
    private DeviceInfoConfig deviceInfoConfig;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 对话型
     *
     */
    public Object createAiQuestion(ChatMsgQo chatMsgQo, String lang, String appId, String sn, String user) {
        String url = deviceInfoConfig.getHttpUrl() + "/api/app/device/ai/v1/" + appId + "/chat";

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("sn", sn);
            httpHeaders.add("X-API-Language", lang);
            httpHeaders.add("user", user);
            return restTemplate.postForObject(url, new HttpEntity<>(chatMsgQo, httpHeaders), String.class);

    }


    public ResponseEntity<String>  commonRequest(String url, HttpMethod method, Object body, Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        if (headersMap != null) {
            headers.setAll(headersMap);
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(deviceInfoConfig.getHttpUrl() + url, method, requestEntity, String.class);
    }



    /**
     * 生成型
     *
     * @param chatMsgQo
     * @param lang
     * @param appId
     * @param sn
     */
    public Object createAiCompletion(ChatMsgQo chatMsgQo, String lang, String appId, String sn, String user) {
        String url = deviceInfoConfig.getHttpUrl() + "/api/app/device/ai/v1/" + appId + "/completion";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("sn", sn);
        httpHeaders.add("X-API-Language", lang);
        httpHeaders.add("user", user);
        return restTemplate.postForObject(url, new HttpEntity<>(chatMsgQo, httpHeaders), String.class);
    }

    /**
     * 运维助手
     *
     * @param username
     * @param role
     * @param token
     * @param vsysId
     * @param fromRootVsys
     * @param language
     * @param version
     * @param sn
     * @param softwareVersion
     * @param branchName
     * @param productFamily
     * @param productName
     * @param harddisk
     * @param mysql
     * @param customerName
     * @param imageName
     * @param llmAccess
     * @param systemtime
     * @param timezone
     * @param timezoneOffset
     * @return
     */
    public String createAiBotQuestion(String username,
                                      String role,
                                      String token,
                                      String vsysId,
                                      String fromRootVsys,
                                      String language,
                                      String version,
                                      String sn,
                                      String softwareVersion,
                                      String branchName,
                                      String productFamily,
                                      String productName,
                                      int harddisk,
                                      int mysql,
                                      String customerName,
                                      String imageName,
                                      int llmAccess,
                                      int systemtime,
                                      String timezone,
                                      int timezoneOffset) {
        String url = deviceInfoConfig.getHttpUrl() + "api/ai/bot/api/session";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Username", username);
        httpHeaders.add("X-Auth-Role", role);
        httpHeaders.add("X-Auth-Token", token);
        httpHeaders.add("X-Auth-VsysId", vsysId);
        httpHeaders.add("X-Auth-Fromrootvsys", fromRootVsys);
        httpHeaders.add("X-Api-Language", language);
        httpHeaders.add("X-Api-Version", version);
        httpHeaders.add("sn", sn);
        httpHeaders.add("software-version", softwareVersion);
        httpHeaders.add("branch-name", branchName);
        httpHeaders.add("product-family", productFamily);
        httpHeaders.add("product-name", productName);
        httpHeaders.add("is-support-harddisk", String.valueOf(harddisk));
        httpHeaders.add("is-support-mysql", String.valueOf(mysql));
        httpHeaders.add("customer-name", customerName);
        httpHeaders.add("image-name", String.valueOf(imageName));
        httpHeaders.add("llm-access", String.valueOf(llmAccess));
        httpHeaders.add("X-API-Systemtime", String.valueOf(systemtime));
        httpHeaders.add("X-Api-Timezone", timezone);
        httpHeaders.add("X-Api-TimezoneOffset", String.valueOf(timezoneOffset));
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        log.info("response: {}", response.getBody());
        return response.getBody();
    }


    /**
     * 给运维助手提问
     *
     * @param username
     * @param role
     * @param token
     * @param vsysId
     * @param fromRootVsys
     * @param language
     * @param version
     * @param sn
     * @param softwareVersion
     * @param branchName
     * @param productFamily
     * @param productName
     * @param harddisk
     * @param mysql
     * @param customerName
     * @param imageName
     * @param llmAccess
     * @param systemtime
     * @param timezone
     * @param timezoneOffset
     * @param botChatS1Qo
     * @return
     */
    public String createAiBotQuestionS1(String username,
                                        String role,
                                        String token,
                                        String vsysId,
                                        String fromRootVsys,
                                        String language,
                                        String version,
                                        String sn,
                                        String softwareVersion,
                                        String branchName,
                                        String productFamily,
                                        String productName,
                                        int harddisk,
                                        int mysql,
                                        String customerName,
                                        String imageName,
                                        int llmAccess,
                                        int systemtime,
                                        String timezone,
                                        int timezoneOffset,
                                        BotChatS1Qo botChatS1Qo) {
        String url = deviceInfoConfig.getHttpUrl() + "api/ai/bot/api/query";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Username", username);
        httpHeaders.add("X-Auth-Role", role);
        httpHeaders.add("X-Auth-Token", token);
        httpHeaders.add("X-Auth-VsysId", vsysId);
        httpHeaders.add("X-Auth-Fromrootvsys", fromRootVsys);
        httpHeaders.add("X-Api-Language", language);
        httpHeaders.add("X-Api-Version", version);
        httpHeaders.add("sn", sn);
        httpHeaders.add("software-version", softwareVersion);
        httpHeaders.add("branch-name", branchName);
        httpHeaders.add("product-family", productFamily);
        httpHeaders.add("product-name", productName);
        httpHeaders.add("is-support-harddisk", String.valueOf(harddisk));
        httpHeaders.add("is-support-mysql", String.valueOf(mysql));
        httpHeaders.add("customer-name", customerName);
        httpHeaders.add("image-name", String.valueOf(imageName));
        httpHeaders.add("llm-access", String.valueOf(llmAccess));
        httpHeaders.add("X-API-Systemtime", String.valueOf(systemtime));
        httpHeaders.add("X-Api-Timezone", timezone);
        httpHeaders.add("X-Api-TimezoneOffset", String.valueOf(timezoneOffset));
        String response = restTemplate.postForObject(url, new HttpEntity<>(botChatS1Qo, httpHeaders), String.class);
        log.info("response: {}", response);
        return response;
    }


}
