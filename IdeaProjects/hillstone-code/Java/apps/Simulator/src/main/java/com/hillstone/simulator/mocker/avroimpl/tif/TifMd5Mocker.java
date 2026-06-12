package com.hillstone.simulator.mocker.avroimpl.tif;

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
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class TifMd5Mocker extends IAvroMocker {

    public TifMd5Mocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "tif";
    }

    @Override
    public String getType() {
        return "md5";
    }

    @Override
    public String getAvroFileName() {
        return "d67e30bb40e82873bba84cdce22e1a65.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "d67e30bb40e82873bba84cdce22e1a65";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    public String QUERY_FIELD = "md5_query";
    public String FROM_FIELD = "from";


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        GenericRecord md5Record = new GenericData.Record(schema);
        try {
            GenericArray<GenericRecord> array = new GenericData.Array<GenericRecord>(10,
                    schema.getField("md5_query").schema());
            GenericRecord record = new GenericData.Record(schema.getField("md5_query").schema().getElementType());
            record.put("element", "fa2c893bdcfccd840d76569fd07901ee");
            record.put(FROM_FIELD, 0);
            array.add(record);
            md5Record.put(QUERY_FIELD, array);
            datumWriter.write(md5Record, encoder);
//            System.out.println(md5Record);


        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
