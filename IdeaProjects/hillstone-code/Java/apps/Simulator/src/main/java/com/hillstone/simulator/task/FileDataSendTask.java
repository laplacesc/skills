package com.hillstone.simulator.task;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.DeviceStatus;
import com.hillstone.simulator.mocker.IFileMocker;
import com.hillstone.simulator.service.UploadDataService;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: bohuachen
 * @date: 2023/6/20 10:52
 * @description: 文件上送任务
 */
@Slf4j
public class FileDataSendTask extends ScheduleTaskAbstract {

    private IFileMocker fileMocker;

    public FileDataSendTask(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName) {
        super(delayTime, intervalTime, device, taskName);
    }

    public FileDataSendTask(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName, IFileMocker fileMocker) {
        super(delayTime, intervalTime, device, taskName);
        this.fileMocker = fileMocker;
    }

    @Override
    public void sendData() {
        if (getDevice().getDeviceStatus().equals(DeviceStatus.REGISTERED)) {
            try {
                SpringUtils.getBean(UploadDataService.class).uploadFile(fileMocker,getDevice());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
