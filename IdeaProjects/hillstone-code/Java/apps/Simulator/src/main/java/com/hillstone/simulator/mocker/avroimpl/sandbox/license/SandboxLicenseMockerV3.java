package com.hillstone.simulator.mocker.avroimpl.sandbox.license;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.sandbox.license.license_info_v3;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/23 14:01
 */
public class SandboxLicenseMockerV3 extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        license_info_v3 value = license_info_v3.newBuilder()
                .setLicenseDue(1L)
                .setFileUploadCap(1)
                .build();

        SpecificDatumWriter<license_info_v3> datumWriter = new SpecificDatumWriter<>(license_info_v3.class);
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
        return "c0dd702b999ac9e779bdd0d743d12c51.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "c0dd702b999ac9e779bdd0d743d12c51";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }
}
