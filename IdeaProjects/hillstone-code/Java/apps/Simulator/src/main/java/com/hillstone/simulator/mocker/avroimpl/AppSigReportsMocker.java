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
 * @date create in 17:34 2024/01/23
 * @description
 */
public class AppSigReportsMocker extends IAvroMocker {
    public AppSigReportsMocker() {
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
        GenericRecord appRecord = new GenericData.Record(schema);
        appRecord.put("device_sn", "2529546225004248");
        appRecord.put("engine_version", "03.87");
        appRecord.put("signature_version", "3.0.231030");
        appRecord.put("release_time", "2023-10-30 16:54");

        GenericArray<GenericRecord> bwTop = new GenericData.Array<>(10, schema.getField("bw_top").schema());
        for (int i = 0; i < 10; i++) {
            GenericRecord genericRecord = new GenericData.Record(schema.getField("bw_top").schema().getElementType());
            genericRecord.put("dstip", "183.198.93." + i);
            genericRecord.put("dport", 25865);
            genericRecord.put("protocol", 6);
            genericRecord.put("domain", "");
            genericRecord.put("bytes", 4969246);
            bwTop.add(genericRecord);
        }
        appRecord.put("bw_top", bwTop);

        GenericArray<GenericRecord> connectTop = new GenericData.Array<>(10, schema.getField("connect_top").schema());
        for (int i = 0; i < 10; i++) {
            GenericRecord genericRecord = new GenericData.Record(schema.getField("connect_top").schema().getElementType());
            genericRecord.put("dstip", "36.147.59." + i);
            genericRecord.put("dport", 443);
            genericRecord.put("protocol", 6);
            genericRecord.put("domain", "");
            genericRecord.put("connections", 10);
            connectTop.add(genericRecord);
        }
        appRecord.put("connect_top", connectTop);

        GenericArray<GenericRecord> application = new GenericData.Array<>(10, schema.getField("application").schema());
        for (int i = 0; i < 10; i++) {
            GenericRecord genericRecord = new GenericData.Record(schema.getField("application").schema().getElementType());
            genericRecord.put("id",  i);
            genericRecord.put("hits", 11);
            application.add(genericRecord);
        }
        appRecord.put("application", application);

        datumWriter.write(appRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "app_report";
    }

    @Override
    public String getType() {
        return "sig_reports";
    }

    @Override
    public String getAvroFileName() {
        return "4944c3ca3e64aec242ad12a264899b6a.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "4944c3ca3e64aec242ad12a264899b6a";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }
}
