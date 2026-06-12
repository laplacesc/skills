package com.hillstone.simulator.mocker.avroimpl.ues;

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
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class UesMd5Mocker extends IAvroMocker {

    public UesMd5Mocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "ues";
    }

    @Override
    public String getType() {
        return "md5";
    }

    @Override
    public String getAvroFileName() {
        return "6311988f2f8bbc210c32e7e044ea078f.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "6311988f2f8bbc210c32e7e044ea078f";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        GenericRecord record = new GenericData.Record(schema);

        record.put("md5", "6c621693e14c368d059323e273e8fedb");
        datumWriter.write(record, encoder);
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
