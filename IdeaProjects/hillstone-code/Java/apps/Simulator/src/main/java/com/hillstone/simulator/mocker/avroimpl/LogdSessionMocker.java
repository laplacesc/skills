package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.logd_session.logd_session.ip_record;
import com.hillstone.simulator.entity.avro.model.logd_session.logd_session.session;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/23 10:52
 */
public class LogdSessionMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        session value = session.newBuilder()
                .setTimestamp(1L)
                .setSyslogId(1)
                .setSrcipA3user("src ip a3 user")
                .setPolicyId(1L)
                .setSip(ip_record.newBuilder().setIpAddr("ip addr").setIpFamily(1).build())
                .setSport(1)
                .setDip(ip_record.newBuilder().setIpAddr("ip addr").setIpFamily(1).build())
                .setDport(1)
                .setProtocolId(1)
                .setActionId(1)
                .setAppName("app name")
                .setLifetime(1L)
                .setInterface$("interface")
                .setSendBytes(1L)
                .setRecvBytes(1L)
                .setSessionCloseReason("session close reason")
                .setPolicyType(1)
                .setPolicyName("policy name")
                .build();

        SpecificDatumWriter<session> datumWriter = new SpecificDatumWriter<>(session.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "logd_session";
    }

    @Override
    public String getType() {
        return "logd_session";
    }

    @Override
    public String getAvroFileName() {
        return "52bfe9652a480341a9e9ed5351124e31.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "52bfe9652a480341a9e9ed5351124e31";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
