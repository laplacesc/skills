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
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class TifTokenMocker extends IAvroMocker {

    public TifTokenMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "tif";
    }

    @Override
    public String getType() {
        return "token";
    }

    @Override
    public String getAvroFileName() {
        return "64f9ecf313b4b6af454fda0812e25a07.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "64f9ecf313b4b6af454fda0812e25a07";
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
        GenericRecord md5Record = new GenericData.Record(schema);
        try {
            md5Record.put("timestamp", System.currentTimeMillis());
            datumWriter.write(md5Record, encoder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
