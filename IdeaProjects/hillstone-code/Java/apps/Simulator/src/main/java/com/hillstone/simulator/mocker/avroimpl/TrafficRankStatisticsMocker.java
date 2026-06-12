package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.statistics_set.traffic_rank.*;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * @author dafeihou
 * @date 2024/1/24 11:20
 */
public class TrafficRankStatisticsMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        traffic_rank value = traffic_rank
                .newBuilder()
                .setUserTrafficRank(
                        traffic_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                traffic_rank_item
                                                        .newBuilder()
                                                        .setUpstreamBandwidth(1L)
                                                        .setDownstreamBandwidth(1L)
                                                        .setItemName("item name")
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
                                .build()
                )
                .setAppTrafficRank(
                        traffic_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                traffic_rank_item
                                                        .newBuilder()
                                                        .setUpstreamBandwidth(1L)
                                                        .setDownstreamBandwidth(1L)
                                                        .setItemName("item name")
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
                                .build()
                )
                .setDipTrafficRank(
                        traffic_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                traffic_rank_item
                                                        .newBuilder()
                                                        .setUpstreamBandwidth(1L)
                                                        .setDownstreamBandwidth(1L)
                                                        .setItemName("item name")
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
                                .build()
                )
                .setInterfaceTrafficRank(
                        traffic_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                traffic_rank_item
                                                        .newBuilder()
                                                        .setUpstreamBandwidth(1L)
                                                        .setDownstreamBandwidth(1L)
                                                        .setItemName("item name")
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
                                .build()
                )
                .build();

        SpecificDatumWriter<traffic_rank> datumWriter = new SpecificDatumWriter<>(traffic_rank.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "statistics_set";
    }

    @Override
    public String getType() {
        return "traffic_rank";
    }

    @Override
    public String getAvroFileName() {
        return "dcee6d48d59358165ac6ec122f67f873.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "dcee6d48d59358165ac6ec122f67f873";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
