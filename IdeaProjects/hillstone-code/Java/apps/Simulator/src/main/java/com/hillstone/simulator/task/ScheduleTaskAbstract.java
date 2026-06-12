package com.hillstone.simulator.task;

import com.hillstone.simulator.config.DeviceInfoConfig;
import lombok.Data;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: bohuachen
 * @date: 2023/6/20 10:24
 * @description: some desc
 */
@Data
public abstract class ScheduleTaskAbstract {

    private int delayTime;
    private int intervalTime;
    private DeviceInfoConfig device;
    private String taskName;

    public static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);


    protected ScheduleTaskAbstract(int delayTime, int intervalTime, DeviceInfoConfig device, String taskName) {
        this.delayTime = delayTime;
        this.intervalTime = intervalTime;
        this.device = device;
        this.taskName = taskName;
    }


    public abstract void sendData();


    public void start() {
        pool.scheduleAtFixedRate(this::sendData, delayTime, intervalTime, TimeUnit.SECONDS);
    }

    public void stop() { // 添加关闭方法
        pool.shutdown();
    }
}
