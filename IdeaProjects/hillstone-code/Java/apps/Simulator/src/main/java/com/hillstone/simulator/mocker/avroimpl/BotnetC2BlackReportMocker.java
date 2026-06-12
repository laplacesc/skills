package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author bhliu
 * @date create in 15:24 2024/01/24
 * @description
 */
public class BotnetC2BlackReportMocker extends IAvroMocker {
    public BotnetC2BlackReportMocker() {
        super();
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        Schema schema;
        try (InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath())) {
            schema = new Schema.Parser().parse(ins);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord botnetReportRecord = new GenericData.Record(schema);
        botnetReportRecord.put("device_sn", "2812746172001142");
        botnetReportRecord.put("engine", "1.0.0");
        botnetReportRecord.put("database", "3.1.231119001");

        GenericArray<GenericRecord> blackInfo = new GenericData.Array<>(10, schema.getField("black_info").schema());
        for (int i = 0; i < 10; i++) {
            GenericRecord record = new GenericData.Record(schema.getField("black_info").schema().getElementType());
            record.put("hit_sig", "178.128.242." + i);
            record.put("def_type", 1);
            record.put("sig_type", 1);
            record.put("hits", 155);
            blackInfo.add(record);
        }

        botnetReportRecord.put("black_info", blackInfo);
        datumWriter.write(botnetReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "botnet_report";
    }

    @Override
    public String getType() {
        return "c2_black";
    }

    @Override
    public String getAvroFileName() {
        return "64ba8533debd2b0cb0e3130a43738c7d.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "64ba8533debd2b0cb0e3130a43738c7d";
    }
}
