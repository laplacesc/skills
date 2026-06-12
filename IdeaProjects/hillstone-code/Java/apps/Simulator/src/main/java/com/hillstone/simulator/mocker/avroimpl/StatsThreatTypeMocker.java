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
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class StatsThreatTypeMocker extends IAvroMocker {

    public StatsThreatTypeMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "stats_threat";
    }

    @Override
    public String getType() {
        return "threat_type";
    }

    @Override
    public String getAvroFileName() {
        return "1a560379cadb6fcd8f12b518fe566d9e.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "1a560379cadb6fcd8f12b518fe566d9e";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord adReportRecord = new GenericData.Record(schema);
        GenericArray<GenericRecord> threatTypeArray = new GenericData.Array<>(10, schema.getField("threat_type").schema());
        for (int i = 0; i < 2; i++) {
            Schema threatSchemaSchema = schema.getField("threat_type").schema();
            GenericRecord threatTypeStatus = new GenericData.Record(threatSchemaSchema.getElementType());
            threatTypeStatus.put("threat_type", i + 1);
            threatTypeStatus.put("time_range", (i + 1));
            threatTypeStatus.put("total_count", 2L);
            GenericArray<GenericRecord> dataArray =
                    new GenericData.Array<>(10, threatSchemaSchema.getElementType().getField("data").schema());
            for (int j = 0; j < 1; j++) {
                GenericRecord threatTypeData = new GenericData.Record(threatSchemaSchema.getElementType().getField("data").schema().getElementType());
                threatTypeData.put("attack_type", j + 1);
                threatTypeData.put("count", (long) (j + 1));
                dataArray.add(threatTypeData);
            }

            threatTypeStatus.put("data", dataArray);
            threatTypeArray.add(threatTypeStatus);
        }
        adReportRecord.put("threat_type", threatTypeArray);
        datumWriter.write(adReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

}
