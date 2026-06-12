package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.iot_monitor_report.iot_monitor.iot_report_upload;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.utils.AvroUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: rtzhang
 * @date: 2023/9/21 5:51
 * @description: some desc
 */
public class IotReportV2Mocker extends IAvroMocker {


    public IotReportV2Mocker() {
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
        return AvroConstant.IOT_REPORT_V2_FILENAME;
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.IOT_REPORT_V2_MD5;
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        List<iot_report_upload> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            iot_report_upload iotReportUpload = new iot_report_upload();
            iotReportUpload.setDeviceId("" + i);
            iotReportUpload.setIp("0.0.0.0");
            iotReportUpload.setMfr("mfrEn");
            iotReportUpload.setMfrCn("厂商中文");
            iotReportUpload.setType("typeEN");
            iotReportUpload.setTypeCn("类型中文");
            iotReportUpload.setDistrict("");
            iotReportUpload.setMac("mac");
            iotReportUpload.setModel("model");
            iotReportUpload.setRx(100L);
            iotReportUpload.setTx(100L);
            list.add(iotReportUpload);
        }
        return AvroUtil.serializeAvroNoHeaderByObject(iot_report_upload.getClassSchema(), list);
    }


}
