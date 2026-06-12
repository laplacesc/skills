package com.hillstone.simulator.mocker;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:45
 * @description: mocker 抽象类 用于avro上送 实现类存放于  com.hillstone.simulator.mocker.avroImpl
 */
@Data
@Slf4j
public abstract class IAvroMocker {

    /**
     * 创建上送文件
     *
     * @param device
     * @return
     * @throws Exception
     */
    public abstract byte[] createDataFile(DeviceInfoConfig device) throws Exception;

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
     * 获取avro schema文件名称
     *
     * @return
     */
    public abstract String getAvroFileName();

    /**
     * 获取任务名称
     *
     * @return
     */
    public String getTaskName() {
        return getCategory() + "-" + getType() + "-" + getAvroMd5();
    }

    /**
     * 上送时间间隔 600s，如果需要修改，可通过实现类重写get方法
     */
    private Integer taskInterval = 600;

    /**
     * 获取avro md5，如果不重写改方法，会通过avsc文件计算出来
     *
     * @return
     */
    public String getAvroMd5() {
        String md5 = "none";
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        try {
            assert ins != null;
            md5 = DigestUtils.md5Hex(ins);
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * avsc 文件需要放入指定路径
     *
     * @return
     */
    public String getAvroFilePath() {
        return AvroConstant.BASE_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

    /**
     * 调用该方法获取
     *
     * @return
     * @throws IOException
     */
    public Schema getSchema() {
        try {
            return new Schema.Parser().parse(this.getClass().getResourceAsStream(this.getAvroFilePath()));
        } catch (Exception e) {
            log.error("获取schema失败");
            return null;
        }
    }
}
