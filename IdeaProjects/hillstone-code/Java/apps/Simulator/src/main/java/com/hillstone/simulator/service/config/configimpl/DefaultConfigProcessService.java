package com.hillstone.simulator.service.config.configimpl;

import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.service.config.ConfigProcessInterface;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: bohuachen
 * @date: 2023/6/25 9:51
 * @description: 默认配置流程实现
 */
@Slf4j
@Service("defaultConfigProcessService")
public class DefaultConfigProcessService implements ConfigProcessInterface {

    @Override
    public void runConfigProcess(MessageObject mo) {
        // 默认只打印下发配置 无需额外处理
        log.info(mo.getXmlString());
    }

    @Override
    public void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel multiDeviceConfigModel) {
        log.debug("设备:{} 收到消息：{}", multiDeviceConfigModel.getSn(),mo.getXmlString());

    }
}
