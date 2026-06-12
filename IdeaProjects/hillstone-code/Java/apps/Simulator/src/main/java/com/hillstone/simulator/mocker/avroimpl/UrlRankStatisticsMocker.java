package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.statistics_set.url_rank.*;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * @author dafeihou
 * @date 2024/1/24 11:33
 */
public class UrlRankStatisticsMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        url_rank value = url_rank
                .newBuilder()
                .setUrlHitRank(
                        url_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                url_rank_item
                                                        .newBuilder()
                                                        .setItemName("item name")
                                                        .setValue(1L)
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
                .setUserHitUrlRank(
                        url_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                url_rank_item
                                                        .newBuilder()
                                                        .setItemName("item name")
                                                        .setValue(1L)
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
                .setUrlCategoryTrafficRank(
                        url_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                url_rank_item
                                                        .newBuilder()
                                                        .setItemName("item name")
                                                        .setValue(1L)
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
                .setUrlCategoryHitRank(
                        url_rank_record
                                .newBuilder()
                                .setRank(
                                        Collections.singletonList(
                                                url_rank_item
                                                        .newBuilder()
                                                        .setItemName("item name")
                                                        .setValue(1L)
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

        SpecificDatumWriter<url_rank> datumWriter = new SpecificDatumWriter<>(url_rank.class);
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
        return "url_rank";
    }

    @Override
    public String getAvroFileName() {
        return "83990295aac4475451de84b12bad0e98.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "83990295aac4475451de84b12bad0e98";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
