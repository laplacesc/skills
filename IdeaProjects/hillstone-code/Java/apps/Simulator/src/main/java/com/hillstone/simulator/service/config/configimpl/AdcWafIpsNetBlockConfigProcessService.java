package com.hillstone.simulator.service.config.configimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.ConfigConstant;
import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.service.config.ConfigProcessInterface;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: rtzhang
 * @date: 2023/6/25 9:51
 * @description: 一键断网
 */
@Slf4j
@Service(ConfigConstant.NET_BLOCK_NAME)
public class AdcWafIpsNetBlockConfigProcessService implements ConfigProcessInterface {
    @Autowired
    private DeviceInfoConfig deviceInfoConfig;

    @Autowired
    private SpringUtils springUtils;

    @Override
    public void runConfigProcess(MessageObject mo) {
        log.info(mo.getXmlString());
        log.info("执行show命令");
        String result = getSuccessResult(mo.getCategory(), mo.getXmlString());
        log.debug("回复设备:{},消息内容:{}", deviceInfoConfig.getSn(), result);

        springUtils.getBean(WebSocketClient.class).send(result);
    }


    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel multiDeviceConfigModel) {
        log.debug(mo.getXmlString());
        log.debug("设备:{} 一键断网", multiDeviceConfigModel.getSn());
        String result = getSuccessResult(mo.getCategory(), mo.getXmlString());
        log.debug("回复设备:{},消息内容:{}", multiDeviceConfigModel.getSn(), result);
        multiDeviceConfigModel.getMultiDeviceWebSocketClient().send(result);
    }

    /**
     * 获取 修改成功后的消息
     *
     * @param category
     * @param xmlStr
     * @return
     */
    public static String getSuccessResult(String category, String xmlStr) {
        switch (category) {
            case ConfigConstant.ADC_UPDATE_CATEGORY: {
                String s1 = xmlStr.replace("<status>1</status>", "<status>1</status><running_status>1</running_status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>true</success></result>");
                String s2 = s1.replace("<status>2</status>", "<status>2</status><running_status>2</running_status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>true</success></result>");
                return s2.replace("<status>3</status>", "<status>3</status><running_status>3</running_status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>true</success></result>");
            }
            case ConfigConstant.IPS_UPDATE_CATEGORY: {
                String s1 = xmlStr.replace("<status>1</status>", "<status>1</status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>true</success></result>");
                return s1.replace("<status>2</status>", "<status>2</status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>true</success></result>");
            }
            case ConfigConstant.WAF_UPDATE_CATEGORY: {
                String s1 = xmlStr.replace("<status>1</status>", "<status>1</status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>true</success></result>");
                return s1.replace("<status>2</status>", "<status>2</status>><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>true</success></result>");
            }
        }
        return xmlStr;
    }

    /**
     * 获取 修改失败后的消息
     *
     * @param category
     * @param xmlStr
     * @return
     */
    public static String getFailResult(String category, String xmlStr) {
        switch (category) {
            case ConfigConstant.ADC_UPDATE_CATEGORY: {
                String s1 = xmlStr.replace("<status>1</status>", "<status>1</status><running_status>1</running_status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>false</success></result>");
                String s2 = s1.replace("<status>2</status>", "<status>2</status><running_status>2</running_status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>false</success></result>");
                return s2.replace("<status>3</status>", "<status>3</status><running_status>3</running_status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>false</success></result>");
            }
            case ConfigConstant.IPS_UPDATE_CATEGORY: {
                String s1 = xmlStr.replace("<status>1</status>", "<status>1</status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>false</success></result>");
                return s1.replace("<status>2</status>", "<status>2</status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>false</success></result>");
            }
            case ConfigConstant.WAF_UPDATE_CATEGORY: {
                String s1 = xmlStr.replace("<status>1</status>", "<status>1</status><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>false</success></result>");
                return s1.replace("<status>2</status>", "<status>2</status>><result><error_code>1</error_code><error_message>xxxxxxxxx</error_message><success>false</success></result>");
            }
        }
        return xmlStr;
    }
}
