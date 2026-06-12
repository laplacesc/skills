package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.logd_report.event_report.event;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:51
 * @description: some desc
 */
public class LogEventReportMockerV2 extends IAvroMocker {

    public LogEventReportMockerV2() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.LOG_EVENT_REPORT_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.LOG_EVENT_REPORT_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return "d67b25d9f218d51c0c145c27620d05fd.avsc";
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.LOG_EVENT_REPORT_MD5;
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        event value = event
                .newBuilder()
                .setTimestamp(1L)
                .setSyslogId(1)
                .setVsysName("vsys name")
                .setVsysId(1)
                .setLogMsg("log msg")
                .build();

        DatumWriter<event> datumWriter = new SpecificDatumWriter<>(event.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();

        return outputStream.toByteArray();
    }

}
