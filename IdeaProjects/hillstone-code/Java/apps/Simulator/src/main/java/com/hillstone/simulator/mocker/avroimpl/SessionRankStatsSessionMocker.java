package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.stats_session.session_rank.sess_user_top;
import com.hillstone.simulator.entity.avro.model.stats_session.session_rank.session_rank;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * @author dafeihou
 * @date 2024/1/24 13:58
 */
public class SessionRankStatsSessionMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        session_rank value = session_rank
                .newBuilder()
                .setTotalSession(1L)
                .setSessUserTop(
                        Collections.singletonList(
                                sess_user_top
                                        .newBuilder()
                                        .setUser("user")
                                        .setSessionNum(1L)
                                        .build()
                        )
                )
                .build();

        SpecificDatumWriter<session_rank> datumWriter = new SpecificDatumWriter<>(session_rank.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        datumWriter.write(value, binaryEncoder);
        binaryEncoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public String getCategory() {
        return "stats_session";
    }

    @Override
    public String getType() {
        return "session_rank";
    }

    @Override
    public String getAvroFileName() {
        return "b35999cc87c5e3a2e0fa8e064a96bd45.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "b35999cc87c5e3a2e0fa8e064a96bd45";
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "." + this.getType() + "/" + this.getAvroFileName();
    }
}
