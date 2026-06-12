package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.logd_sandbox.logd_sandbox.sandbox;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/23 10:37
 */
public class LogdSandboxMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        sandbox value = sandbox.newBuilder()
                .setTimestamp(1L)
                .setSyslogId(1)
                .setReceiveTime(1L)
                .setFileName("file name")
                .setSip("sip")
                .setSport(1)
                .setDip("dip")
                .setDport(1)
                .setDir("dir")
                .setUrl("url")
                .setApp("app")
                .setFileType("file type")
                .setDetectBy("detect by")
                .setVerdict("verdict")
                .setProfileName("profile name")
                .build();

        DatumWriter<sandbox> datumWriter = new SpecificDatumWriter<>(sandbox.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "logd_sandbox";
    }

    @Override
    public String getType() {
        return "log_sandbox";
    }

    @Override
    public String getAvroFileName() {
        return "2591c540cb5987277a29ed7c2a0c8fd2.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "2591c540cb5987277a29ed7c2a0c8fd2";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }
}
