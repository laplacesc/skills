package com.hillstone.simulator.mocker.avroimpl.sandbox.license;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.sandbox.license.license_info_v4;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/23 14:01
 */
public class SandboxLicenseMockerV4 extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        license_info_v4 value = license_info_v4.newBuilder()
                .setLicenseDue(1L)
                .setFileUploadCap(1)
                .setLicenseType(1)
                .setThreatShare(1)
                .build();

        SpecificDatumWriter<license_info_v4> datumWriter = new SpecificDatumWriter<>(license_info_v4.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "sandbox";
    }

    @Override
    public String getType() {
        return "license";
    }

    @Override
    public String getAvroFileName() {
        return "e1852864cfeb35d1cbd5181c39f1205d.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "e1852864cfeb35d1cbd5181c39f1205d";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }
}
