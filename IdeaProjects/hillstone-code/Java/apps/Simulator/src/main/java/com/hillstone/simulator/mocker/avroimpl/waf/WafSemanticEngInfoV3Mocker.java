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
public class WafSemanticEngInfoV3Mocker extends IAvroMocker {

    public WafSemanticEngInfoV3Mocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "waf_report";
    }

    @Override
    public String getType() {
        return "waf_semantic_eng_info";
    }

    @Override
    public String getAvroFileName() {
        return "ae05a332ac2c87e66155eab7bf6bf07e.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "ae05a332ac2c87e66155eab7bf6bf07e";
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
        genericRecord.put("time", System.currentTimeMillis()/1000L);
        genericRecord.put("client_ip", "58.53.207.91");
        genericRecord.put("client_port", 40833);
        genericRecord.put("server_ip", "10.192.138.13");
        genericRecord.put("server_port", 443);
        genericRecord.put("site_id", 84);
        genericRecord.put("site_name", "市政府-https");
        genericRecord.put("policy_name", "policy_custom");
        genericRecord.put("rule_id", 1030030000);
        genericRecord.put("rule_name", "xss_xss:based_on_semantic_analysis");
        genericRecord.put("desc_en", "XSS Injection Attack Attempts: Detection based on semantic analysis");
        genericRecord.put("desc_cn", "检测到XSS注入攻击尝试：基于语义分析的检测");
        genericRecord.put("severity", 1);
        genericRecord.put("domain_name", "www.wuhan.gov.cn");
        genericRecord.put("http_method", "GET");
        genericRecord.put("http_url", "/ztzl/ztfwcx/jyky/zyjy_83447/wtjd/202301/%252B%20encodeURIComponent(url));%0A%20%20%7D%0A%3C/script%3E%20%3C!--%20add%20by%20kslee%20in%2020210222%20dianjiliangtongji%20--%3E%20%0A%20%20%3Cscript%20id=");
        genericRecord.put("http_content", "GET /ztzl/ztfwcx/jyky/zyjy_83447/wtjd/202301/%252B%20encodeURIComponent(url));%0A%20%20%7D%0A%3C/script%3E%20%3C!--%20add%20by%20kslee%20in%2020210222%20dianjiliangtongji%20--%3E%20%0A%20%20%3Cscript%20id= HTTP/1.1\\r\\nConnection: close\\r\\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\\r\\nReferer: https://www.wuhan.gov.cn/ztzl/ztfwcx/jyky/zyjy_83447/wtjd/202301/t20230113_2133070.shtml\\r\\nAccept-Charset: UTF-8,GBK,GB2312,GB18030,BIG5,ISO-8859-1,UTF-16,UTF-32\\r\\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\\r\\nAccept-Encoding: gzip, deflate\\r\\nAccept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\\r\\nUpgrade-Insecure-Requests: 1\\r\\nHost: www.wuhan.gov.cn\\r\\nCookie: token=d08f50e2-f85a-4a14-8fd7-5b11ead3b4c9; uuid=d08f50e2-f85a-4a14-8fd7-5b11ead3b4c9\\r\\n\\r\\n\\r\\n\\r\\n");
        genericRecord.put("src_country", "CN");
        genericRecord.put("src_area", "HB");
        genericRecord.put("payload", "/ztzl/ztfwcx/jyky/zyjy_83447/wtjd/202301/+ encodeURIComponent(url));\n");
        genericRecord.put("quotes_decision", "");
        genericRecord.put("unmatched_short_fp", "");
        genericRecord.put("fingerprint", "");
        genericRecord.put("semantic_engine_version", "");
        genericRecord.put("semantic_engine_result", "");
        genericRecord.put("waf_engine_version", "");
        genericRecord.put("waf_rule_version", "");

        datumWriter.write(genericRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }
}
