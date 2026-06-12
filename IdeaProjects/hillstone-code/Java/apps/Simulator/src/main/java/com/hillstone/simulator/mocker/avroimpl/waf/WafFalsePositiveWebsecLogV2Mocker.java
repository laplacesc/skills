package com.hillstone.simulator.mocker.avroimpl.waf;

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
public class WafFalsePositiveWebsecLogV2Mocker extends IAvroMocker {

    public WafFalsePositiveWebsecLogV2Mocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "waf_false_positive_websec";
    }

    @Override
    public String getType() {
        return "waf_false_positive_websec_log";
    }

    @Override
    public String getAvroFileName() {
        return "8c9868457c99ee6d457eef87805a733f.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "8c9868457c99ee6d457eef87805a733f";
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
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord genericRecord = new GenericData.Record(schema);
        genericRecord.put("time", 1585734229084L);
        genericRecord.put("client_ip", "1.0.0.0");
        genericRecord.put("client_port", 8888);
        genericRecord.put("server_ip", "2.0.0.0");
        genericRecord.put("server_port", 9999);
        genericRecord.put("site_id", 111);
        genericRecord.put("site_name", "site_name");
        genericRecord.put("policy_name", "policy_name");
        genericRecord.put("protection_type", 1111111l);

        genericRecord.put("protection_sub_type", 1585734229084l);
        genericRecord.put("action", "action");
        genericRecord.put("followed_action", "followed_action");
        genericRecord.put("block_time", "block_time");
        genericRecord.put("rule_id", 7777);
        genericRecord.put("rule_name", "rule_name");
        genericRecord.put("desc_en", "desc_en");
        genericRecord.put("desc_cn", "desc_cn");
        genericRecord.put("severity", 555);
        genericRecord.put("domain_name", "domain_name");

        genericRecord.put("http_method", "http_method");

        genericRecord.put("http_url", "http_url");
        genericRecord.put("http_content", "http_content");
        genericRecord.put("src_country", "src_country");
        genericRecord.put("src_area", "src_area");
        genericRecord.put("message", "message");
        genericRecord.put("hit_count", 66666L);
        genericRecord.put("desc_feedback", "desc_feedback");


        datumWriter.write(genericRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }
}
