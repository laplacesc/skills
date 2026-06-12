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
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class StatsUrkRankMocker extends IAvroMocker {

    public StatsUrkRankMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "stats_url";
    }

    @Override
    public String getType() {
        return "url_rank";
    }

    @Override
    public String getAvroFileName() {
        return "874b3f15321aeb60cf7eb588f771cf80.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "874b3f15321aeb60cf7eb588f771cf80";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord adReportRecord = new GenericData.Record(schema);

        GenericArray<GenericRecord> urlUrlTopArray = new GenericData.Array<>(10, schema.getField("url_url_top").schema());
        GenericRecord urlUrlTop = new GenericData.Record(schema.getField("url_url_top").schema().getElementType());
        urlUrlTop.put("url", "p0.qhimg.com");
        urlUrlTop.put("hit", 22L);
        urlUrlTopArray.add(urlUrlTop);
        adReportRecord.put("url_url_top",urlUrlTopArray);

        GenericArray<GenericRecord> urlCategoryTopArray = new GenericData.Array<>(10, schema.getField("url_category_top").schema());
        GenericRecord urlCategoryTop = new GenericData.Record(schema.getField("url_category_top").schema().getElementType());
        urlCategoryTop.put("url_cate_name", "Uncategorized");
        urlCategoryTop.put("hit", 22L);
        urlCategoryTopArray.add(urlCategoryTop);
        adReportRecord.put("url_category_top",urlCategoryTopArray);

        GenericArray<GenericRecord> urlUserTopArray = new GenericData.Array<>(10, schema.getField("url_user_top").schema());
        GenericRecord urlUserTop = new GenericData.Record(schema.getField("url_user_top").schema().getElementType());
        urlUserTop.put("user", "192.168.0.123");
        urlUserTop.put("hit", 22L);
        urlUserTopArray.add(urlUserTop);
        adReportRecord.put("url_user_top",urlUserTopArray);

        datumWriter.write(adReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

}
