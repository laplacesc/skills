package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.statistics_set.session_rank.*;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * @author dafeihou
 * @date 2024/1/24 10:24
 */
public class SessionRankStatisticsMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        session_rank value = session_rank
                .newBuilder()
                .setUserSessionRank(
                        session_rank_record
                                .newBuilder()
                                .setTotalSession(1L)
                                .setSessionRankItems(
                                        Collections.singletonList(
                                                session_rank_item.newBuilder().setItemName("item name").setValue(1L).build()
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
                .setAppSessionRank(
                        session_rank_record
                                .newBuilder()
                                .setTotalSession(1L)
                                .setSessionRankItems(
                                        Collections.singletonList(
                                                session_rank_item.newBuilder().setItemName("item name").setValue(1L).build()
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

        SpecificDatumWriter<session_rank> datumWriter = new SpecificDatumWriter<>(session_rank.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);
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
        return "session_rank";
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
