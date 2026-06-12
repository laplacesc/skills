package com.hillstone.simulator.mocker.avroimpl.statistics.device_info;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.statistics.device_info.v1.*;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * @author dafeihou
 * @date 2024/1/23 15:56
 */
public class DeviceInfoMockerV1 extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        device_info value = device_info
                .newBuilder()
                .setOverallTraffic(
                        diff_inet_family_traffic
                                .newBuilder()
                                .setTrafficIpv4(directional_traffic_record.newBuilder().setDownstreamBandwidth(1L).setUpstreamBandwidth(1L).build())
                                .setTrafficIpv6(directional_traffic_record.newBuilder().setDownstreamBandwidth(1L).setUpstreamBandwidth(1L).build())
                                .build()
                )
                .setOverallSession(
                        diff_inet_family_value
                                .newBuilder()
                                .setIpv4Value(1L)
                                .setIpv6Value(1L)
                                .build()
                )
                .setOverallSessionRampup(
                        diff_inet_family_value
                                .newBuilder()
                                .setIpv4Value(1L)
                                .setIpv6Value(1L)
                                .build()
                )
                .setAllUserVisiableInterfacesInfo(
                        Collections.singletonList(
                                interfaces_info
                                        .newBuilder()
                                        .setIfName("name")
                                        .setTraffic(
                                                diff_inet_family_traffic
                                                        .newBuilder()
                                                        .setTrafficIpv4(directional_traffic_record.newBuilder().setUpstreamBandwidth(1L).setDownstreamBandwidth(1L).build())
                                                        .setTrafficIpv6(directional_traffic_record.newBuilder().setUpstreamBandwidth(1L).setDownstreamBandwidth(1L).build())
                                                        .build()
                                        )
                                        .build()
                        )
                )
                .setJoinControlInfo(
                        control_info
                                .newBuilder()
                                .setInterval(1)
                                .setEndTime(1L)
                                .setCollectDataStatus(status.SUCCESS)
                                .setAdditionalInfo("additional info")
                                .build()
                )
                .build();

        SpecificDatumWriter<device_info> datumWriter = new SpecificDatumWriter<>(device_info.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "statistics";
    }

    @Override
    public String getType() {
        return "device_info";
    }

    @Override
    public String getAvroFileName() {
        return "7dd1bdaecfbfa1d6c66859299204f8fe.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "7dd1bdaecfbfa1d6c66859299204f8fe";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
