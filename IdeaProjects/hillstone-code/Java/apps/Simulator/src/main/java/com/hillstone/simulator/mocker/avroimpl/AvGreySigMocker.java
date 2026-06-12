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
 * @date create in 10:26 2024/01/24
 * @description
 */
public class AvGreySigMocker extends IAvroMocker {
    public AvGreySigMocker() {
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
        GenericRecord avGreySigRecord = new GenericData.Record(schema);
        avGreySigRecord.put("device_sn", "5828223225001040");
        avGreySigRecord.put("engine", "ANT 2.5.0");
        avGreySigRecord.put("database", "2.5.220904");

        GenericArray<GenericRecord> signatures = new GenericData.Array<>(10, schema.getField("signatures").schema());
        for (int i = 0; i < 10; i++) {
            GenericRecord record = new GenericData.Record(schema.getField("signatures").schema().getElementType());
            record.put("id", "ad72604790e2f3ac9cd9a8168c2d90" + i);
            signatures.add(record);
        }

        avGreySigRecord.put("signatures", signatures);
        datumWriter.write(avGreySigRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "av_report";
    }

    @Override
    public String getType() {
        return "grey_sig";
    }

    @Override
    public String getAvroFileName() {
        return "197d23166352ec0043c5afffd40babda.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "197d23166352ec0043c5afffd40babda";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }
}
