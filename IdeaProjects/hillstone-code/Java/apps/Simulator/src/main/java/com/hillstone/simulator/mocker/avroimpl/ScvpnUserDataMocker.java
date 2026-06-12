package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.scvpn_user.scvpn_user_data.sslvpn;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/23 14:48
 */
public class ScvpnUserDataMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        sslvpn value = sslvpn.newBuilder()
                .setSaIndex(1)
                .setUserName("user name")
                .setVsysName("vsys name")
                .setTunnelName("tunnel name")
                .setPublicIp("public ip")
                .setPrivateIp("private ip")
                .setUserGroup("user group")
                .setDeviceType("device type")
                .setHostid("host id")
                .setOs("os")
                .setMac("mac")
                .setVersion("version")
                .setLoginTime(1L)
                .setLogoffTime(1L)
                .setOnlineTime(1L)
                .setTrafficRecv(1L)
                .setTrafficSend(1L)
                .setTimestamp(1L)
                .build();

        SpecificDatumWriter<sslvpn> datumWriter = new SpecificDatumWriter<>(sslvpn.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "scvpn_user";
    }

    @Override
    public String getType() {
        return "scvpn_user_data";
    }

    @Override
    public String getAvroFileName() {
        return "73b43a90a909e7ac3c05146536780663.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "73b43a90a909e7ac3c05146536780663";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }
}
