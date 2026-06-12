package com.hillstone.simulator.task;

import com.hillstone.simulator.client.DeviceWebSocketClient;
import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.DeviceStatus;
import com.hillstone.simulator.utils.FileUtils;
import com.hillstone.simulator.utils.SpringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author: bohuachen
 * @date: 2023/6/20 10:17
 * @description: 心跳上送
 */
public class BasicMonitorRelaTimeSendTask extends ScheduleTaskAbstract {

    public BasicMonitorRelaTimeSendTask(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName) {
        super(delayTime, intervalTime, device, taskName);
    }

    @Override
    public void sendData() {
        WebSocket session = SpringUtils.getBean(DeviceWebSocketClient.class);
        DeviceInfoConfig device = SpringUtils.getBean(DeviceInfoConfig.class);
        if (session.isOpen() && device.getDeviceStatus().equals(DeviceStatus.REGISTERED)) {
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
                //sendTrapMessage(session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendTrapMessage(WebSocket session){
        String trapMessage = "<msg category=\"Trap\"><event-class>data</event-class><severity>major</severity><content><signature-library-modify><index>3</index><name>APP</name><version>3.0.240328</version><release-time>2024-03-28</release-time><edition>Standard</edition></signature-library-modify></content></msg>";
        session.send(trapMessage);
    }


}
