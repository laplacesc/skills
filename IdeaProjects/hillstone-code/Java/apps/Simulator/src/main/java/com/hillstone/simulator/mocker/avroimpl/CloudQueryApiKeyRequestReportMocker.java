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
 * @author jxwu
 */
public class CloudQueryApiKeyRequestReportMocker extends IAvroMocker {
    public CloudQueryApiKeyRequestReportMocker() {
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
        GenericRecord genericRecord = new GenericData.Record(schema);
        genericRecord.put("device_sn", "WASD382354820500");
        datumWriter.write(genericRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "cloud_query";
    }

    @Override
    public String getType() {
        return "api_key_request";
    }

    @Override
    public String getAvroFileName() {
        return "e195e1c8c8239b990cc658c438dd201e.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "e195e1c8c8239b990cc658c438dd201e";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }
}
