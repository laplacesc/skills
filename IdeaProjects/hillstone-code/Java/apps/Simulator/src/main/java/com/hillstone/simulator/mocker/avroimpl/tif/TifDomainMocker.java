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
import java.util.Set;

/**
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class TifDomainMocker extends IAvroMocker {

    public TifDomainMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "tif";
    }

    @Override
    public String getType() {
        return "domain";
    }

    @Override
    public String getAvroFileName() {
        return "2170b23108e93ecb867ab3bd73c85b71.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "2170b23108e93ecb867ab3bd73c85b71";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    public String QUERY_FIELD = "domain_query";
    public String FROM_FIELD = "from";

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        GenericRecord md5Record = new GenericData.Record(schema);

        Set<String> domains = domainSet();
        try {
            GenericArray<GenericRecord> array = new GenericData.Array<GenericRecord>(domains.size(),
                    schema.getField(QUERY_FIELD).schema());
            for (String domain : domains) {
                GenericRecord record = new GenericData.Record(schema.getField(QUERY_FIELD).schema().getElementType());
                record.put("element", domain);
                record.put(FROM_FIELD, 0);
                array.add(record);
            }
            md5Record.put(QUERY_FIELD, array);
            datumWriter.write(md5Record, encoder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    public static Set<String> domainSet() {

        Set<String> domainSet = new HashSet<>();
//        domainSet.add("www.wikipedia.org");
        domainSet.add("www.baidu.com");
//        domainSet.add("www.qq.com");
//        domainSet.add("www.163.com");
//        domainSet.add("sina.com");
//        domainSet.add("docs.spring.io");
//        domainSet.add("neo4j.com");
//        domainSet.add("avro-cli.readthedocs.io");
//        domainSet.add("www.arangodb.com");
//        domainSet.add("mp.weixin.qq.com");
//        domainSet.add("baike.baidu.com");
//        domainSet.add("cn.bing.com");
//        domainSet.add("translate.google.cn");
//        domainSet.add("oss.redislabs.com");
//        domainSet.add("segmentfault.com");

//        StringBuilder builder = new StringBuilder("baidu.com.");
//
//        for (int i = 0; i < 1024; i++) {
//            builder.append("s");
//        }
//        domainSet.add(builder.toString());
//         domainSet.addAll(getDomainFromFile());
        return domainSet;
    }

}
