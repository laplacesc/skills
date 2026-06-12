package com.hillstone.simulator.task;

import com.hillstone.simulator.client.MultiDeviceWebSocketClient;
import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.DeviceStatus;
import com.hillstone.simulator.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author: bohuachen
 * @date: 2023/6/20 10:17
 * @description: 心跳上送
 */
@Slf4j
public class MultiMonitorTask extends ScheduleTaskAbstract {
    MultiDeviceWebSocketClient session;

    public MultiMonitorTask(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName, MultiDeviceWebSocketClient webSocketClient) {
        super(delayTime, intervalTime, device, taskName);
        this.session = webSocketClient;
    }

    @Override
    public void sendData() {
        DeviceInfoConfig device = this.getDevice();
        if (session.isOpen() && device.getDeviceStatus().equals(DeviceStatus.REGISTERED)) {
            log.info("发送心跳：{}", device.getSn());
            String[] realTimeFileNames = {"1.xml", "2.xml"};
            try {
                String ret;
                int i = RandomUtils.nextInt(realTimeFileNames.length);
                String fileName = realTimeFileNames[i];
                InputStream ins = this.getClass()
                        .getResourceAsStream("/basicxml/deviceBasicMonitorRealTimeData/" + device.getRealTimeMonitorVersion() + "/" + fileName);
                ret = device.getFilenameContent().get(fileName);
                if (ret == null) {
                    ret = FileUtils.replaceDeviceInfo(ins);
                    device.getFilenameContent().put(fileName, ret);
                }
                session.send(ret);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
