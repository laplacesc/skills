package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.ptf_report.ip_hit.report;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/23 13:45
 */
public class PtfReportIpHitMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        report value = report.newBuilder()
                .setIp(1L)
                .setIsSrc(1)
                .setHitTime(1L)
                .build();

        SpecificDatumWriter<report> specificDatumWriter = new SpecificDatumWriter<>(report.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        specificDatumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "ptf_report";
    }

    @Override
    public String getType() {
        return "ip_hit";
    }

    @Override
    public String getAvroFileName() {
        return "d8a9f00c43f7387f42de9ad92969a734.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "d8a9f00c43f7387f42de9ad92969a734";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
