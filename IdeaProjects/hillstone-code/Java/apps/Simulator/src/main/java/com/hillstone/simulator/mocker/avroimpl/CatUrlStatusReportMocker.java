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
 * @date create in 17:44 2024/01/24
 * @description
 */
public class CatUrlStatusReportMocker extends IAvroMocker {
    public CatUrlStatusReportMocker() {
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
        botnetReportRecord.put("license_expire_time", "1970-01-01 08:00:00");
        botnetReportRecord.put("database_version", "");
        botnetReportRecord.put("feature_status", 4);
        botnetReportRecord.put("url_number", 0);
        botnetReportRecord.put("url_mem_size", 0);
        botnetReportRecord.put("flash_memory_size", 0);

        GenericArray<GenericRecord> urlScanAlgor = new GenericData.Array<>(1, schema.getField("url_scan_algor").schema());
        GenericRecord record = new GenericData.Record(schema.getField("url_scan_algor").schema().getElementType());
        record.put("type", 0);
        record.put("using_mem", 67108864);
        urlScanAlgor.add(record);

        botnetReportRecord.put("url_scan_algor", urlScanAlgor);
        datumWriter.write(botnetReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "caturl_report";
    }

    @Override
    public String getType() {
        return "caturl_status";
    }

    @Override
    public String getAvroFileName() {
        return "d2687aa2c2d8b8862b023e6b814ccb5f.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "d2687aa2c2d8b8862b023e6b814ccb5f";
    }
}
