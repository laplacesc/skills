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
 * @date create in 11:35 2024/01/24
 * @description
 */
public class BotnetStatusReportMocker extends IAvroMocker {
    public BotnetStatusReportMocker() {
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
        botnetReportRecord.put("device_sn", "5833348225016757");
        botnetReportRecord.put("license_expire_time", "2028-03-12 00:00:00");
        botnetReportRecord.put("database_version", "3.6.231026001");
        botnetReportRecord.put("feature_status", 3);
        botnetReportRecord.put("C2_signature_number", 600000);
        botnetReportRecord.put("C2_signature_mem_size", 130796888);
        botnetReportRecord.put("DGA_mem_size", 6292320);
        botnetReportRecord.put("DNS_tunnel_mem_size", 76800);
        botnetReportRecord.put("flash_mem_size", 37068803);

        GenericArray<GenericRecord> botnetScanAlgor = new GenericData.Array<>(1, schema.getField("botnet_scan_algor").schema());
        GenericRecord record = new GenericData.Record(schema.getField("botnet_scan_algor").schema().getElementType());
        record.put("type", 0);
        record.put("using_mem", 22383744);
        botnetScanAlgor.add(record);

        botnetReportRecord.put("botnet_scan_algor", botnetScanAlgor);
            datumWriter.write(botnetReportRecord, encoder);
            encoder.flush();
            output.close();
            return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getAvroFileName() {
        return null;
    }
}
