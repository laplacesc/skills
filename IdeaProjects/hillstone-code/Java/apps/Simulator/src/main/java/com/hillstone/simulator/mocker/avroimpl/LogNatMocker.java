package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.Schema;
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
 * @date create in 11:25 2024/01/25
 * @description
 */
public class LogNatMocker extends IAvroMocker {
    public LogNatMocker() {
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
        GenericRecord logRecord = new GenericData.Record(schema);
        logRecord.put("timestamp", 1700532173);
        logRecord.put("syslog_id", 1179444763);
        logRecord.put("srcip_a3user", "-");
        logRecord.put("type", 3);
        logRecord.put("rule_id", 1);

        GenericRecord sipRecord = new GenericData.Record(schema.getField("sip").schema());
        sipRecord.put("ip_addr", "10.84.245.93");
        sipRecord.put("ip_family", 1);
        logRecord.put("sip", sipRecord);

        logRecord.put("sport", 35300);
        logRecord.put("dport", 35300);
        logRecord.put("trans_port", 35300);
        logRecord.put("source_trans_port", 35300);
        logRecord.put("protocol_id", 6);

        GenericRecord dipRecord = new GenericData.Record(schema.getField("dip").schema());
        dipRecord.put("ip_addr", "10.84.245.93");
        dipRecord.put("ip_family", 1);
        logRecord.put("dip", dipRecord);

        GenericRecord transIpRecord = new GenericData.Record(schema.getField("trans_ip").schema());
        transIpRecord.put("ip_addr", "10.84.245.93");
        transIpRecord.put("ip_family", 1);
        logRecord.put("trans_ip", transIpRecord);

        GenericRecord sourceTransIpRecord = new GenericData.Record(schema.getField("source_trans_ip").schema());
        sourceTransIpRecord.put("ip_addr", "10.84.245.93");
        sourceTransIpRecord.put("ip_family", 1);
        logRecord.put("source_trans_ip", sourceTransIpRecord);

        datumWriter.write(logRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "logd_nat";
    }

    @Override
    public String getType() {
        return "log_nat";
    }

    @Override
    public String getAvroFileName() {
        return "e347ea61262f76a5a3d6c7d059f2140c.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "e347ea61262f76a5a3d6c7d059f2140c";
    }
}

