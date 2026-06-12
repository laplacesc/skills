package com.hillstone.simulator.mocker.avroimpl.tif;

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
public class TifLicenseMocker extends IAvroMocker {

    public TifLicenseMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "tif";
    }

    @Override
    public String getType() {
        return "license";
    }

    @Override
    public String getAvroFileName() {
        return "fa2c893bdcfccd840d76569fd07901ee.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "fa2c893bdcfccd840d76569fd07901ee";
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

        record.put("license_due", System.currentTimeMillis() / 1000 + 3600);
        record.put("license_type", 1);
        datumWriter.write(record, encoder);
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
