package com.hillstone.simulator.mocker.avroimpl.sandbox.md5;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.sandbox.md5.v2.md5_query_with_protocol_context;
import com.hillstone.simulator.entity.avro.model.sandbox.md5.v2.sandbox_icloud_md5_query_with_protocol_context;
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
public class SandboxMd5MockerV2 extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        md5_query_with_protocol_context value = md5_query_with_protocol_context.newBuilder()
                .setMd5QueryWithProtocolContext(
                        Collections.singletonList(
                                sandbox_icloud_md5_query_with_protocol_context
                                        .newBuilder()
                                        .setMd5("md5")
                                        .setFileType("file type")
                                        .setProtocol("protocol")
                                        .setSrcIp("src ip")
                                        .setSrcPort("src port")
                                        .setDstIp("dst ip")
                                        .setDstPort("dst port")
                                        .setVersion("version")
                                        .setData("data")
                                        .build()
                        )
                )
                .build();

        SpecificDatumWriter<md5_query_with_protocol_context> datumWriter = new SpecificDatumWriter<>(md5_query_with_protocol_context.class);
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
        return "cc4f1479fca57494ec4439264de3ceda.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "cc4f1479fca57494ec4439264de3ceda";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }
}
