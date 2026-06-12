package com.hillstone.simulator.task;

import com.hillstone.simulator.config.DeviceInfoConfig;

/**
 * @author xjhuang
 * @date create in 19:58 2024/4/16
 * @description
 */
public class DeviceTrapMessageSendTask extends ScheduleTaskAbstract{

    private String type;

    public DeviceTrapMessageSendTask(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName, String type) {
        super(delayTime, intervalTime, device, taskName);
        this.type = type;
    }

    @Override
    public void sendData() {
    }
}
