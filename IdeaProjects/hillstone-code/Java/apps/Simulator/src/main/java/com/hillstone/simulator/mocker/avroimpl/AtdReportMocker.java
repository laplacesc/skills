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
 * @date create in 11:12 2024/01/24
 * @description
 */
public class AtdReportMocker extends IAvroMocker {
    public AtdReportMocker() {
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
        GenericRecord atdReportRecord = new GenericData.Record(schema);
        atdReportRecord.put("uid", 1033440);
        atdReportRecord.put("malware_family_id", 1731);
        atdReportRecord.put("malware_family_distance", 0);
        atdReportRecord.put("malware_family_name", "Trojan[PSW]/Win32.Update");
        atdReportRecord.put("domain_name", "yclib.hunnu.edu.cn");
        atdReportRecord.put("http_uri", "/error/500?code=504&ip=66.249.69.193&url=http%3A%2F%2Fyclib.hunnu.edu.cn%2Fvpn%2F1008%2Fhttps%2FP75YPLUDPW3GK7LUF3SX85B%2Farticles%2F179123-morphometry-and-morphology-of-the-acromion-process-and-its-implications-in-subacromial-impingement-syndrome");
        atdReportRecord.put("http_uri_match", "");
        atdReportRecord.put("http_ua", "Mediapartners-Google");
        atdReportRecord.put("http_ua_match", "Mediapartners-Google");
        atdReportRecord.put("malware_family_sig", "");
        atdReportRecord.put("malware_family_hash", 0);

        datumWriter.write(atdReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "atd";
    }

    @Override
    public String getType() {
        return "final_threat_report";
    }

    @Override
    public String getAvroFileName() {
        return "2b903c11ec20cd6569f772e96995a095.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "2b903c11ec20cd6569f772e96995a095";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }
}
