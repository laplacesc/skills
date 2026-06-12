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
 * @date create in 15:53 2024/01/24
 * @description
 */
public class BotnetC2GreyReportMocker extends IAvroMocker {
    public BotnetC2GreyReportMocker() {
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
        botnetReportRecord.put("device_sn", "3411400000000040");
        botnetReportRecord.put("engine", "1.0.0");
        botnetReportRecord.put("database", "3.8.231120001");

        GenericArray<GenericRecord> blackInfo = new GenericData.Array<>(10, schema.getField("grey_info").schema());
        for (int i = 0; i < 10; i++) {
            GenericRecord record = new GenericData.Record(schema.getField("grey_info").schema().getElementType());
            record.put("sig", "122.193.87." + i);
            record.put("hits", 10);
            blackInfo.add(record);
        }

        botnetReportRecord.put("grey_info", blackInfo);
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
        return "c2_grey";
    }

    @Override
    public String getAvroFileName() {
        return "a7a365f3416a7e0fbd42f7ecca4fc024.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "a7a365f3416a7e0fbd42f7ecca4fc024";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }


}
