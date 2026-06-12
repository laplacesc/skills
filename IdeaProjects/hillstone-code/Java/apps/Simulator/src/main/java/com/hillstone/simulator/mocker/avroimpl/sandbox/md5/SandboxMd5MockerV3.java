package com.hillstone.simulator.mocker.avroimpl.sandbox.md5;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.sandbox.md5.v3.md5_query;
import com.hillstone.simulator.entity.avro.model.sandbox.md5.v3.sandbox_icloud_md5_query;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * @author dafeihou
 * @date 2024/1/23 14:31
 */
public class SandboxMd5MockerV3 extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        md5_query value = md5_query
                .newBuilder()
                .setMd5Query(
                        Collections.singletonList(
                                sandbox_icloud_md5_query
                                        .newBuilder()
                                        .setFileType("file type")
                                        .setMd5("md5")
                                        .setProtocol("protocol")
                                        .build()
                        )
                )
                .build();

        SpecificDatumWriter<md5_query> datumWriter = new SpecificDatumWriter<>(md5_query.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);
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
        return "md5";
    }

    @Override
    public String getAvroFileName() {
        return "d8e0b89ded23fd9dcefa1a87e50b6681.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "d8e0b89ded23fd9dcefa1a87e50b6681";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }
}
