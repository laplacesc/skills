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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class TifIpMocker extends IAvroMocker {

    public TifIpMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "tif";
    }

    @Override
    public String getType() {
        return "ip";
    }

    @Override
    public String getAvroFileName() {
        return "92510c2a50b565fa8ba0447a9e0b5ea3.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "92510c2a50b565fa8ba0447a9e0b5ea3";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    public String QUERY_FIELD = "ip_query";
    public String FROM_FIELD = "from";

    public static String getIp() {
        // String[] ips = new String[]{"123.150.76.177", "112.80.48.55", "112.80.48.56", "112.80.48.57", "112.80.48.58", "112.80.48.59", "112.80.48.60", "112.80.48.61","114.114.114.114"};
        String[] ips = new String[]{"2.3.7.157"};
        Random r = new Random();
        return ips[r.nextInt(ips.length)];
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        GenericRecord md5Record = new GenericData.Record(schema);

        Set<String> ips = new HashSet<>();
        for(int i=0;i<5;i++){
            ips.add(getIp());
        }
        try {
            GenericArray<GenericRecord> array = new GenericData.Array<GenericRecord>(ips.size(),
                    schema.getField(QUERY_FIELD).schema());
            for (String ip : ips) {
                GenericRecord record = new GenericData.Record(schema.getField(QUERY_FIELD).schema().getElementType());
                record.put("element", ip);
                record.put(FROM_FIELD, 0);
                array.add(record);
            }
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
