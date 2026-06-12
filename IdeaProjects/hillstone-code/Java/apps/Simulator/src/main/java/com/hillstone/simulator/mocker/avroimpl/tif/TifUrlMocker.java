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
public class TifUrlMocker extends IAvroMocker {

    public TifUrlMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "tif";
    }

    @Override
    public String getType() {
        return "url";
    }

    @Override
    public String getAvroFileName() {
        return "834003e8d213a2b13fb2c67bec114c01.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "834003e8d213a2b13fb2c67bec114c01";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    public String QUERY_FIELD = "url_query";
    public String FROM_FIELD = "from";

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        GenericRecord md5Record = new GenericData.Record(schema);

        Set<String> urls = generateUrl();
        try {
            GenericArray<GenericRecord> array = new GenericData.Array<GenericRecord>(urls.size(),
                    schema.getField(QUERY_FIELD).schema());
            for (String url : urls) {
                GenericRecord record = new GenericData.Record(schema.getField(QUERY_FIELD).schema().getElementType());
                record.put("element", url);
                record.put(FROM_FIELD, 0);
                array.add(record);
//                System.out.println(new Date() + " url_query " + url);
            }
            md5Record.put(QUERY_FIELD, array);
//            md5Record.put(FROM_FIELD, AvroConstant.TI_QUERY_FROM);
            datumWriter.write(md5Record, encoder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }
    public static Set<String> generateUrl() {
        Set<String> urlSet = new HashSet<>();
        urlSet.add("https://help.aliyun.com/knowledge_detail/40094.html?spm=a2c6h.13066369.0.0.49aa42e5AXF1mk");
        urlSet.add("https://www.jianshu.com");
        urlSet.add("https://cloudview.hillstonenet.com.cn");
        urlSet.add("https://www.qq.com");
        urlSet.add("http://js.qq.com/a/20200214/002916.htm");

//        urlSet.add("http://baidu.com");
//        urlSet.add("http://hillstonenet.com.cn");
//        urlSet.add("http://sina.com/");
//        urlSet.add("https://docs.spring.io/spring-kafka/docs/2.2.8.RELEASE/reference/html/#example");
//        urlSet.add("https://neo4j.com/docs/getting-started/current/get-started-with-neo4j/");
//        urlSet.add("https://avro-cli.readthedocs.io/en/latest/");
//        urlSet.add("https://www.arangodb.com/why-arangodb/sql-aql-comparison?name=123&age=23");
//        urlSet.add("https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=中文12");
        return urlSet;
    }
}
