package com.hillstone.simulator.service.config;

import com.hillstone.simulator.constant.ConfigProcessBeanEnum;
import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author: bohuachen
 * @date: 2023/6/20 9:43
 * @description: 设备配置服务
 */
@Slf4j
@Service
public class DeviceConfigService {


    /**
     * 不可处理数据返回false
     *
     * @param mo
     * @return
     */
    public void messageProcess(MessageObject mo, MultiDeviceConfigModel multiDeviceConfigModel) {
        boolean result = false;
        try {
            result = cloudConfigProcess(mo, multiDeviceConfigModel);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
        if (!result) {
            log.debug("appserver can't deal with this message");
        }
    }

    /**
     * 云平台配置处理
     *
     * @param mo
     * @return
     */
    private boolean cloudConfigProcess(MessageObject mo, MultiDeviceConfigModel multiDeviceConfigModel) {
        String dataType = mo.getCategory() + "-" + mo.getType();
        log.info("dataType:{}", dataType);
        log.info("receive device msg :{}", mo.getXmlString());
        if (null != ConfigProcessBeanEnum.getConfigProcess(dataType)) {
            if (!Objects.isNull(multiDeviceConfigModel)) {
                SpringUtils.getBean(ConfigProcessBeanEnum.getConfigProcess(dataType), ConfigProcessInterface.class).runMultiConfigProcess(mo, multiDeviceConfigModel);
            } else {
                SpringUtils.getBean(ConfigProcessBeanEnum.getConfigProcess(dataType), ConfigProcessInterface.class).runConfigProcess(mo);
            }
            return true;
        }
        return false;
    }

}
