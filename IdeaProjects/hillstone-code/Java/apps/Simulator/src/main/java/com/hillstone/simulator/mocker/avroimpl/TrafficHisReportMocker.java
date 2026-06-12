package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.sset_report.traffic_his.report;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/23 15:41
 */
public class TrafficHisReportMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        report value = report
                .newBuilder()
                .setType(1)
                .setName("name")
                .setVr("vr")
                .setUpBytes(1L)
                .setDownBytes(1L)
                .build();

        SpecificDatumWriter<report> datumWriter = new SpecificDatumWriter<>(report.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "sset_report";
    }

    @Override
    public String getType() {
        return "traffic_his";
    }

    @Override
    public String getAvroFileName() {
        return "376d0eb483aca4f5c1e81fc1a2e1c622.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "376d0eb483aca4f5c1e81fc1a2e1c622";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
