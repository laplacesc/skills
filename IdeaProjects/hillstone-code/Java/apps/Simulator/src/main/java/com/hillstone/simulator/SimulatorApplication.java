package com.hillstone.simulator;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.SimulatorConstant;
import com.hillstone.simulator.service.multi.MultiDeviceService;
import com.hillstone.simulator.service.single.SingleDeviceRegisterService;
import com.hillstone.simulator.utils.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;


/**
 * @author: bohuachen
 * @date: 2023/6/29 10:52
 * @description: 启动类
 */
@EnableScheduling
@SpringBootApplication
public class SimulatorApplication {


    public static void main(String[] args) {
        SpringApplication.run(SimulatorApplication.class, args);
        // 模式为自动 服务启动自动创建设备
        if (SpringUtils.getBean(DeviceInfoConfig.class).getProcessModel() == SimulatorConstant.PROCESS_MODE_AUTO) {
            MultiDeviceService multiDeviceService = null;
            try {
                multiDeviceService = SpringUtils.getBean(MultiDeviceService.class);
            } catch (Exception ignored) {
                // do nothing
            }
            //单台
            if (Objects.isNull(multiDeviceService)) {
                SpringUtils.getBean(SingleDeviceRegisterService.class).deviceRegister();
            } else {
                try {
                    multiDeviceService.startAll();

                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
        }
    }

}
