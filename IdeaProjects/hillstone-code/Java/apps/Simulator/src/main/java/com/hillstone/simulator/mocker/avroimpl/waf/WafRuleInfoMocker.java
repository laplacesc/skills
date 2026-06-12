package com.hillstone.simulator.mocker.avroimpl.waf;

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
public class WafRuleInfoMocker extends IAvroMocker {

    public WafRuleInfoMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "waf_rule";
    }

    @Override
    public String getType() {
        return "waf_rule_info";
    }

    @Override
    public String getAvroFileName() {
        return "d6beaf795e1d2bf3ad04adc192778f8d.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "d6beaf795e1d2bf3ad04adc192778f8d";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        Schema schema;
        try (InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath())) {
            schema = new Schema.Parser().parse(ins);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord wafDEfacementRecord = new GenericData.Record(schema);
        wafDEfacementRecord.put("waf_rule_version", "1.0");
        wafDEfacementRecord.put("date_start", (long)15);
        wafDEfacementRecord.put("date_end", (long)15);
//        wafDEfacementRecord.put("waf_engine_version", "1.8.1");
        wafDEfacementRecord.put("waf_engine_version", "1");


        GenericArray<GenericRecord> siteArray = new GenericData.Array<GenericRecord>(10, schema.getField("waf_rule_info").schema());
        for (int i=0; i < 10; i++) {
            GenericRecord data = new GenericData.Record(schema.getField("waf_rule_info").schema().getElementType());
            data.put("rule_id", (long)i);
            data.put("hit_counts", (long)99);
            siteArray.add(data);

        }

        wafDEfacementRecord.put("waf_rule_info", siteArray);
        datumWriter.write(wafDEfacementRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }
}
