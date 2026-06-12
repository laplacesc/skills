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
import java.util.Date;

/**
 * @author: bohuachen
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class WafWebSecMocker extends IAvroMocker {

    public WafWebSecMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "waf_report";
    }

    @Override
    public String getType() {
        return "waf_websec";
    }

    @Override
    public String getAvroFileName() {
        return "428a8ee011638e7c098774649536bf5e.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "428a8ee011638e7c098774649536bf5e";
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
        for (int i = 1; i < 10; i++){
            GenericRecord data = new GenericData.Record(schema);

            data.put("time", new Date().getTime());
            data.put("waf_engine_version","1.2.3");
            data.put("waf_rule_version", "1.2.1");
            data.put("client_ip", "192.168.20.52");
            data.put("client_port", 41859);
            data.put("server_ip", "192.168.50.103");
            data.put("server_port", 80);
            data.put("site_id", 16);
            data.put("site_name", "靶机");
            data.put("policy_name", "policy_emergency");
            data.put("protection_type", 199);
            data.put("protection_sub_type", 19901);
            data.put("action", "observe");
            data.put("followed_action", "NONE");
            data.put("block_time", "");
            data.put("rule_id", 1990000001);
            data.put("rule_name", "none_web_attack_capture");
            data.put("desc_en", "重保模式自动生成规则，用于记录非web攻击日志，退出重保模式时会自动删除。");
            data.put("desc_cn", "重保模式自动生成规则，用于记录非web攻击日志，退出重保模式时会自动删除。");
            data.put("severity", 5);
            data.put("domain_name", "192.168.50.103");
            data.put("http_method", "GET");
            data.put("http_url", "/");
            data.put("http_content", "\"GET / HTTP/1.0\\r\\nHost: 192.168.50.103:80\\r\\nUser-Agent: HealthCheckClient\\r\\n\\r\\n\\r\\n\\r\\nHTTP/1.1 200 OK\\r\\nDate: Tue, 21 Nov 2023 01:59:26 GMT\\r\\nServer: Apache/2.4.23 (Win32) OpenSSL/1.0.2j PHP/5.4.45\\r\\nX-Powered-By: PHP/5.4.45\\r\\nContent-Type: text/html; charset=utf-8\\r\\nConnection: close\\r\\n\\r\\n\"");
            data.put("src_country", "IIP");
            data.put("src_area", "");
            data.put("message", "非Web攻击信息记录");
            data.put("hit_count", 1);
            data.put("matched_pattern", "aaa");
            data.put("decoded_methods", "bbb");

            datumWriter.write(data, encoder);
//            System.out.println(data);
        }
        encoder.flush();
        output.close();
        return output.toByteArray();
    }
}
