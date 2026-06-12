package com.hillstone.simulator.task;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.DeviceStatus;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.service.UploadDataService;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: bohuachen
 * @date: 2023/6/20 10:52
 * @description: avro 上送任务
 */
@Slf4j
public class AvroDataSendTask extends ScheduleTaskAbstract {

    private IAvroMocker avroMocker;

    public AvroDataSendTask(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName) {
        super(delayTime, intervalTime, device, taskName);
    }

    public AvroDataSendTask(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName, IAvroMocker avroMocker) {
        super(delayTime, intervalTime, device, taskName);
        this.avroMocker = avroMocker;
    }

    @Override
    public void sendData() {
        if (getDevice().getDeviceStatus().equals(DeviceStatus.REGISTERED)) {
            try {
                SpringUtils.getBean(UploadDataService.class).uploadAvro(avroMocker.getAvroMd5(),getDevice());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
