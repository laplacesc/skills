package com.hillstone.simulator.service.config.configimpl;

import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.service.config.ConfigProcessInterface;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: bohuachen
 * @date: 2023/6/25 9:51
 * @description: 推送逻辑id
 */
@Slf4j
@Service("reqLogicIdProcessService")
public class ReqLogicIdProcessService implements ConfigProcessInterface {

    @Autowired
    private SpringUtils springUtils;

    @Override
    public void runConfigProcess(MessageObject mo) {
        log.info("回复logicId消息");

        /**
         * 依据情况手动配置
         */
        String logicId = "1111222233334444";

        String msg = "<msg category=\"Register\" type=\"LogicID\">" +
                "  <logicId>" + logicId + "</logicId>" +
                "</msg>";
        log.info(msg);
        springUtils.getBean(WebSocketClient.class).send(msg);
    }

    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel multiDeviceConfigModel) {
        /**
         * 依据情况手动配置
         */
        String logicId = "1111222233334444";

        String msg = "<msg category=\"Register\" type=\"LogicID\">" +
                "  <logicId>" + logicId + "</logicId>" +
                "</msg>";
        log.debug("设备:{}, 发送消息：{}", multiDeviceConfigModel.getSn(), msg);
        multiDeviceConfigModel.getMultiDeviceWebSocketClient().send(msg);
    }


}
