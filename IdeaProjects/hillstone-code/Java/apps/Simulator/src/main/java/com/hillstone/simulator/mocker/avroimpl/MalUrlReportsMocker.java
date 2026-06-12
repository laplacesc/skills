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
 * @date: 2023/6/19 5:51
 * @description: some desc
 */
public class MalUrlReportsMocker extends IAvroMocker {


    public MalUrlReportsMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "malurl_report";
    }

    @Override
    public String getType() {
        return "sig_reports";
    }

    @Override
    public String getAvroFileName() {
        return "bfdae73629890de4ab0135e86765816c.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "bfdae73629890de4ab0135e86765816c";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
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
        GenericRecord ipsStatusRecord = new GenericData.Record(schema);
        ipsStatusRecord.put("device_sn", "1234");
        ipsStatusRecord.put("engine", "1234");
        ipsStatusRecord.put("database", "1.2.3");


        GenericArray<GenericRecord> idp_scan_algor = new GenericData.Array<>(10, schema.getField("signatures").schema());

        for (int i = 0; i < 10; i++) {
            GenericRecord ips_status_idp_scan_algor = new GenericData.Record(schema.getField("signatures").schema().getElementType());
            ips_status_idp_scan_algor.put("id", "id" + i);
            ips_status_idp_scan_algor.put("hits", i + 10);
            idp_scan_algor.add(ips_status_idp_scan_algor);
        }
        ipsStatusRecord.put("signatures", idp_scan_algor);
        datumWriter.write(ipsStatusRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

}
