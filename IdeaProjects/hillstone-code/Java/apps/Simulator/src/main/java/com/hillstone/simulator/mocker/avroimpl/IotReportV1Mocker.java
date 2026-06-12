package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.iot_monitor_report.iot_monitor.v1.iot_monitor_report_to_cloudview;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.utils.AvroUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: rtzhang
 * @date: 2023/9/21 5:51
 * @description: some desc
 */
public class IotReportV1Mocker extends IAvroMocker {


    public IotReportV1Mocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.IOT_REPORT_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.IOT_REPORT_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.IOT_REPORT_V1_FILENAME;
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.IOT_REPORT_V1_MD5;
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        List<iot_monitor_report_to_cloudview> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            iot_monitor_report_to_cloudview iotMonitorReportToCloudview = new iot_monitor_report_to_cloudview();
            iotMonitorReportToCloudview.setDeviceId("" + i);
            iotMonitorReportToCloudview.setIp("0.0.0.0");
            iotMonitorReportToCloudview.setMfr(1);//取值范围0-35 参考厂商列表
            iotMonitorReportToCloudview.setType(1);//取值范围0-16 参考设备类型列表
            iotMonitorReportToCloudview.setDistrict("");
            iotMonitorReportToCloudview.setMac("mac");
            iotMonitorReportToCloudview.setModel("model");
            iotMonitorReportToCloudview.setRx(100L);
            iotMonitorReportToCloudview.setTx(100L);
            list.add(iotMonitorReportToCloudview);
        }
        return AvroUtil.serializeAvroNoHeaderByObject(iot_monitor_report_to_cloudview.getClassSchema(), list);
    }


}
