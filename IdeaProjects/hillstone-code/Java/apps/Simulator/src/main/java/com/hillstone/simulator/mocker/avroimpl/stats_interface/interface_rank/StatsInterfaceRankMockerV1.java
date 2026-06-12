package com.hillstone.simulator.mocker.avroimpl.stats_interface.interface_rank;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.stats_interface.interface_rank.v1.interface_item;
import com.hillstone.simulator.entity.avro.model.stats_interface.interface_rank.v1.stats_interface;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * @author dafeihou
 * @date 2024/1/24 13:49
 */
public class StatsInterfaceRankMockerV1 extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        stats_interface value = stats_interface
                .newBuilder()
                .setInterface$(
                        Collections.singletonList(
                                interface_item
                                        .newBuilder()
                                        .setInterface$("interface")
                                        .setUpavgspeed(1L)
                                        .setDownavgspeed(1L)
                                        .setUpmaxspeed(1L)
                                        .setDownmaxspeed(1L)
                                        .build()
                        )
                )
                .setInterval(1)
                .build();

        SpecificDatumWriter<stats_interface> datumWriter = new SpecificDatumWriter<>(stats_interface.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "stats_interface";
    }

    @Override
    public String getType() {
        return "interface_rank";
    }

    @Override
    public String getAvroFileName() {
        return "ab9bf7af3370d1ab9f8dedf4d0109ba0.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "ab9bf7af3370d1ab9f8dedf4d0109ba0";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
