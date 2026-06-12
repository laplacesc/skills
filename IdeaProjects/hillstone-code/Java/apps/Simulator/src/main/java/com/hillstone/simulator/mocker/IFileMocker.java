package com.hillstone.simulator.mocker;

import com.hillstone.simulator.config.DeviceInfoConfig;
import lombok.Data;

import java.io.IOException;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:45
 * @description: mocker 抽象类 用于文件上送 实现类存放于  com.hillstone.simulator.mocker.fileImpl
 */
@Data
public abstract class IFileMocker {

    /**
     * 创建上送文件（可以直接使用文件，跳过生成文件的步骤）
     *
     * @param device 设备信息
     * @return
     * @throws IOException
     */
     public abstract byte[] createFileData(DeviceInfoConfig device) throws IOException;

    /**
     * 获取category
     *
     * @return
     */
     public abstract String getCategory();


    /**
     * 获取type
     *
     * @return
     */
     public abstract String getType();

    /**
     * 获取上送文件MD5
     *
     * @return
     */
     public abstract String getFileMd5();

    /**
     * 获取taskName
     *
     * @return
     */
     public abstract String getTaskName();

    /**
     * 获取上送文件时间间隔
     */
    private Integer taskInterval = 600;


}
