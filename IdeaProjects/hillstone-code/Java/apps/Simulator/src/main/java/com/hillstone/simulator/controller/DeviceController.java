package com.hillstone.simulator.controller;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.SimulatorConstant;
import com.hillstone.simulator.entity.http.ResponseTemplate;
import com.hillstone.simulator.entity.http.ai.AiResponse;
import com.hillstone.simulator.entity.qo.BotChatS1Qo;
import com.hillstone.simulator.entity.qo.ChatMsgQo;
import com.hillstone.simulator.entity.qo.CreateDeviceQo;
import com.hillstone.simulator.service.DeviceService;
import com.hillstone.simulator.service.UploadDataService;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;
import com.hillstone.simulator.service.multi.MultiDeviceRegister;
import com.hillstone.simulator.service.multi.SnWebsocket;
import com.hillstone.simulator.utils.JacksonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bohuachen
 * @date 2025/3/12 17:40
 * @description
 */
@Slf4j
@RestController
@RequestMapping("simulator")
public class DeviceController {

    @Getter
    private final Map<String, MultiDeviceConfigModel> websocketMap = new HashMap<>();

    @Autowired
    private SnWebsocket snWebsocket;
    @Autowired
    private DeviceInfoConfig deviceInfoConfig;
    @Autowired
    private UploadDataService uploadDataService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RestTemplate restTemplate;



    /**
     * 创建设备
     *
     * @param createDeviceQo
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("create-device")
    public ResponseTemplate<String> deviceRegister(@RequestBody CreateDeviceQo createDeviceQo) throws URISyntaxException {

        if (websocketMap.containsKey(createDeviceQo.getSn())) {
            return new ResponseTemplate<>(0, "device exist");
        }
        MultiDeviceConfigModel model = new MultiDeviceConfigModel(createDeviceQo.getSn(), createDeviceQo.getUsername(), snWebsocket.createWebSocketClient());
        DeviceInfoConfig config = deviceInfoConfig.deepCopy();
        config.setSn(createDeviceQo.getSn());
        config.setUsername(createDeviceQo.getUsername());
        config.setFwName(createDeviceQo.getFwName());
        config.setPlatform(createDeviceQo.getPlatform());
        config.setBootFile(createDeviceQo.getBootFile());

        new MultiDeviceRegister(model, config, uploadDataService).deviceRegister();
        websocketMap.put(createDeviceQo.getSn(), model);

        return new ResponseTemplate<>(SimulatorConstant.RESPONSE_OK, "success");
    }


    /**
     * 获取设备在线状态
     *
     * @param sn
     * @return
     */
    @GetMapping("get-device-status")
    public ResponseTemplate<Boolean> getDeviceStatus(@RequestParam String sn) {
        return new ResponseTemplate<>(websocketMap.containsKey(sn) ? SimulatorConstant.RESPONSE_OK : SimulatorConstant.RESPONSE_EMPTY, websocketMap.containsKey(sn));
    }

    /**
     * 离线设备
     *
     * @param sn
     * @return
     */
    @GetMapping("device-offline")
    public ResponseTemplate<String> deviceOffline(@RequestParam String sn) {
        if (websocketMap.containsKey(sn)) {
            websocketMap.get(sn).getMultiDeviceWebSocketClient().close();
        }
        return new ResponseTemplate<>(SimulatorConstant.RESPONSE_OK, "success");
    }

    @PostMapping("ai/common")
    public ResponseEntity<String> commonRequest(@RequestHeader("appId") String appId,
                                                @RequestHeader("method") String method,
                                                @RequestHeader("sn") String sn,
                                                @RequestHeader("lang") String lang,
                                                @RequestHeader("user") String user,
                                                @RequestHeader("path") String path,
                                                @RequestBody Object body) {
        log.info("appId {} lang: {} user:{}, device:{}, question:{}", appId, lang, user, sn, JacksonUtil.toJson(body));
        AiResponse response = new AiResponse();
        if (!websocketMap.containsKey(sn)) {
            // 设备离线
            response.setCode(0);
            response.setMessage("device not exist");
            return ResponseEntity.of(java.util.Optional.ofNullable(JacksonUtil.toJson(response)));
        }
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("sn", sn);
        httpHeaders.put("X-API-Language", lang);
        httpHeaders.put("user", user);
        return deviceService.commonRequest(path, HttpMethod.resolve(method), body, httpHeaders);
    }

    @GetMapping("ai/v1/{appId}/messages")
    public ResponseEntity<String> session(@PathVariable("appId") String appId,
                                          @RequestHeader("sn") String sn,
                                          @RequestHeader("lang") String lang,
                                          @RequestHeader("user") String user,
                                          @RequestParam String sessionId,
                                          @RequestParam(required = false) String messageId,
                                          @RequestParam(required = false) Integer limit) {
        AiResponse response = new AiResponse();
        if (!websocketMap.containsKey(sn)) {
            // 设备离线
            response.setCode(0);
            response.setMessage("device not exist");
            return ResponseEntity.of(java.util.Optional.ofNullable(JacksonUtil.toJson(response)));
        }
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("sn", sn);
        httpHeaders.put("X-API-Language", lang);
        httpHeaders.put("user", user);
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(httpHeaders);

        String path = deviceInfoConfig.getHttpUrl() + "api/app/device/ai/v1/" + appId + "/messages" + "?sessionId=" + sessionId;
        if (messageId != null) {
            path += "&messageId=" + messageId;
        }
        if (limit != null) {
            path += "&limit=" + limit;
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(path, HttpMethod.GET, requestEntity, String.class);
    }


    @GetMapping("ai/v1/{appId}/suggestion")
    public ResponseEntity<String> suggest(@PathVariable("appId") String appId,
                                          @RequestHeader("sn") String sn,
                                          @RequestHeader("lang") String lang,
                                          @RequestHeader("user") String user,
                                          @RequestParam String sessionId) {
        AiResponse response = new AiResponse();
        if (!websocketMap.containsKey(sn)) {
            // 设备离线
            response.setCode(0);
            response.setMessage("device not exist");
            return ResponseEntity.of(java.util.Optional.ofNullable(JacksonUtil.toJson(response)));
        }
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("sn", sn);
        httpHeaders.put("X-API-Language", lang);
        httpHeaders.put("user", user);
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(httpHeaders);

        String path = deviceInfoConfig.getHttpUrl() + "api/app/device/ai/v1/" + appId + "/suggestion" + "?sessionId=" + sessionId;

        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(path, HttpMethod.GET, requestEntity, String.class);
    }


    /**
     * 创建ai问题-对话型
     *
     * @param chatMsgQo
     * @return
     */
    @PostMapping("ai/v1/{appId}/chat-messages")
    public Object createAiQuestion(@PathVariable("appId") String appId,
                                   @RequestHeader("sn") String sn,
                                   @RequestHeader("lang") String lang,
                                   @RequestHeader("user") String user,
                                   @RequestBody ChatMsgQo chatMsgQo) {
        log.info("appId {} lang: {} user:{}, device:{}, question:{}", appId, lang, user, sn, chatMsgQo);
        AiResponse response = new AiResponse();
        if (!websocketMap.containsKey(sn)) {
            // 设备离线
            response.setCode(0);
            response.setMessage("device not exist");
            return JacksonUtil.toJson(response);
        }
        return deviceService.createAiQuestion(chatMsgQo, lang, appId, sn, user);
    }

    /**
     * 创建ai问题-生成型
     *
     * @param chatMsgQo
     * @return
     */
    @PostMapping("ai/v1/{appId}/completion")
    public Object createAiCompletion(@PathVariable("appId") String appId,
                                         @RequestHeader("sn") String sn,
                                         @RequestHeader("lang") String lang,
                                         @RequestHeader("user") String user,
                                         @RequestBody ChatMsgQo chatMsgQo) {
        log.info("appId {} lang: {} user:{}, device:{}, question:{}", appId, lang, user, sn, chatMsgQo);
        AiResponse response = new AiResponse();
        if (!websocketMap.containsKey(sn)) {
            // 设备离线
            response.setCode(0);
            response.setMessage("device not exist");
            return response;
        }
        return deviceService.createAiCompletion(chatMsgQo, lang, appId, sn, user);
    }


    /**
     * 运维助手访问
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
    @GetMapping("ai/bot/api/v1/session")
    public String createAiBotQuestion(@RequestHeader("X-Auth-Username") String username,
                                      @RequestHeader("X-Auth-Role") String role,
                                      @RequestHeader("X-Auth-Token") String token,
                                      @RequestHeader("X-Auth-VsysId") String vsysId,
                                      @RequestHeader("X-Auth-Fromrootvsys") String fromRootVsys,
                                      @RequestHeader("X-Api-Language") String language,
                                      @RequestHeader("X-Api-Version") String version,
                                      @RequestHeader("sn") String sn,
                                      @RequestHeader("software-version") String softwareVersion,
                                      @RequestHeader("branch-name") String branchName,
                                      @RequestHeader("product-family") String productFamily,
                                      @RequestHeader("product-name") String productName,
                                      @RequestHeader(value = "is-support-harddisk", defaultValue = "1") int harddisk,
                                      @RequestHeader(value = "is-support-mysql", defaultValue = "1") int mysql,
                                      @RequestHeader(value = "customer-name", defaultValue = "Hillstone Internal Use Only") String customerName,
                                      @RequestHeader(value = "image-name", defaultValue = "SG6000-A-1-5.5R10P7.19-v6") String imageName,
                                      @RequestHeader(value = "llm-access", defaultValue = "0") int llmAccess,
                                      @RequestHeader("X-API-Systemtime") int systemtime,
                                      @RequestHeader("X-Api-Timezone") String timezone,
                                      @RequestHeader("X-Api-TimezoneOffset") int timezoneOffset) {
        AiResponse response = new AiResponse();
        if (!websocketMap.containsKey(sn)) {
            // 设备离线
            response.setCode(0);
            response.setMessage("device not exist");
            return JacksonUtil.toJson(response);
        }
        return deviceService.createAiBotQuestion(username, role, token, vsysId, fromRootVsys, language, version, sn, softwareVersion, branchName, productFamily, productName, harddisk, mysql, customerName, imageName, llmAccess, systemtime, timezone, timezoneOffset);
    }


    /**
     * 运维助手访问
     *
     * @param botChatS1Qo
     * @return
     */
    @PostMapping("ai/bot/api/v1/query")
    public String createAiBotQuestionS1(@RequestHeader("X-Auth-Username") String username,
                                        @RequestHeader("X-Auth-Role") String role,
                                        @RequestHeader("X-Auth-Token") String token,
                                        @RequestHeader("X-Auth-VsysId") String vsysId,
                                        @RequestHeader("X-Auth-Fromrootvsys") String fromRootVsys,
                                        @RequestHeader("X-Api-Language") String language,
                                        @RequestHeader("X-Api-Version") String version,
                                        @RequestHeader("sn") String sn,
                                        @RequestHeader("software-version") String softwareVersion,
                                        @RequestHeader("branch-name") String branchName,
                                        @RequestHeader("product-family") String productFamily,
                                        @RequestHeader("product-name") String productName,
                                        @RequestHeader(value = "is-support-harddisk", defaultValue = "1") int harddisk,
                                        @RequestHeader(value = "is-support-mysql", defaultValue = "1") int mysql,
                                        @RequestHeader(value = "customer-name", defaultValue = "Hillstone Internal Use Only") String customerName,
                                        @RequestHeader(value = "image-name", defaultValue = "SG6000-A-1-5.5R10P7.19-v6") String imageName,
                                        @RequestHeader(value = "llm-access", defaultValue = "0") int llmAccess,
                                        @RequestHeader("X-API-Systemtime") int systemtime,
                                        @RequestHeader("X-Api-Timezone") String timezone,
                                        @RequestHeader("X-Api-TimezoneOffset") int timezoneOffset,
                                        @RequestBody BotChatS1Qo botChatS1Qo) {
        return deviceService.createAiBotQuestionS1(username, role, token, vsysId, fromRootVsys, language, version, sn, softwareVersion, branchName, productFamily, productName, harddisk, mysql, customerName, imageName, llmAccess, systemtime, timezone, timezoneOffset, botChatS1Qo);
    }


}
