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
 * @date create in 17:21 2024/01/24
 * @description
 */
public class BotnetDgaDomainReportMocker extends IAvroMocker {
    public BotnetDgaDomainReportMocker() {
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
        botnetReportRecord.put("database", "3.8.231120001");

        GenericArray<GenericRecord> result = new GenericData.Array<>(1, schema.getField("result").schema());
        GenericRecord record = new GenericData.Record(schema.getField("result").schema().getElementType());
        record.put("domain", "api.jdcuixcjmdzsjifcswdei.com");
        record.put("family", "p2pgoz");
        record.put("timestamp", 1700558522);
        record.put("addr1", "");
        record.put("addr2", "");
        record.put("addr3", "");
        record.put("addr4", "");
        record.put("hits", 1);
        result.add(record);

        botnetReportRecord.put("result", result);
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
        return "dga_domain";
    }

    @Override
    public String getAvroFileName() {
        return "9fbd186a16cec8496c01c37619a3845d.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "9fbd186a16cec8496c01c37619a3845d";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }
}
