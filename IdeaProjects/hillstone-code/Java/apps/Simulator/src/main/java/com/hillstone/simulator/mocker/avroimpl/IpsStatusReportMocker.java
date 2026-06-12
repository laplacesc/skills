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
 * @date create in 11:25 2024/01/25
 * @description
 */
public class IpsStatusReportMocker extends IAvroMocker {
    public IpsStatusReportMocker() {
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
        GenericRecord ipsRecord = new GenericData.Record(schema);
        ipsRecord.put("device_sn", "2508321174029953");
        ipsRecord.put("license_expire_time", "2022-07-08 08:00:00");
        ipsRecord.put("database_version", "3.0.115");
        ipsRecord.put("feature_status", 3);
        ipsRecord.put("ips_engine_version", 703);
        ipsRecord.put("flash_memory_size", 108712);

        GenericArray<GenericRecord> idpScanAlgor = new GenericData.Array<>(1, schema.getField("idp_scan_algor").schema());
        GenericRecord genericRecord = new GenericData.Record(schema.getField("idp_scan_algor").schema().getElementType());
        genericRecord.put("type", 1);
        genericRecord.put("using_mem", 169993);
        genericRecord.put("max_mem", 169993);
        genericRecord.put("longest_mem", 13148);
        idpScanAlgor.add(genericRecord);
        ipsRecord.put("idp_scan_algor", idpScanAlgor);
        datumWriter.write(ipsRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "ips_report";
    }

    @Override
    public String getType() {
        return "ips_status";
    }

    @Override
    public String getAvroFileName() {
        return "1a5b5cfeac08cd2b769cee55ca8ba57d.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "1a5b5cfeac08cd2b769cee55ca8ba57d";
    }
}

