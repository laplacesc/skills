package com.hillstone.simulator.mocker.avroimpl.stats_interface.interface_rank;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.stats_interface.interface_rank.v2.report;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

/**
 * @author dafeihou
 * @date 2024/1/24 13:49
 */
public class StatsInterfaceRankMockerV2 extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        report value = report
                .newBuilder()
                .setInterface$("interface")
                .setUpavgspeed(1L)
                .setDownavgspeed(1L)
                .setUpmaxspeed(1L)
                .setDownmaxspeed(1L)
                .setInterval(1)
                .build();

        SpecificDatumWriter<report> datumWriter = new SpecificDatumWriter<>(report.class);
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
        return "f55709129f48ccd5afc4a24589bf58a6.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "f55709129f48ccd5afc4a24589bf58a6";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
