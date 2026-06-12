package com.hillstone.simulator.mocker.avroimpl.waf;


import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
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

/**
 * adc的虚拟服务器，对应一键断网功能
 */
public  class WafSiteDataMocker extends IAvroMocker {


    public WafSiteDataMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.WAF_SITE_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.WAF_SITE_CONFIG_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.WAF_SITE_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.WAF_SITE_MD5;
    }

    @Override
    public Integer getTaskInterval(){
        return 60;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {

        Schema schema = getSchema();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);

        GenericRecord wafSiteRecord = new GenericData.Record(schema);
        wafSiteRecord.put("_capacity", 1);
        GenericArray<GenericRecord> siteArray = new GenericData.Array<GenericRecord>(10, schema.getField("waf_sites").schema());
        for (int i = 0; i < 10; i++) {
            GenericRecord data = new GenericData.Record(schema.getField("waf_sites").schema().getElementType());
            data.put("id", i + 1);
            data.put("name", "site" + (i + 1));
            data.put("site_type", 0);
            data.put("status", 1);
            data.put("policy", 1);
            data.put("ip_port_domain", "{\"ip_port\":[{\"ip_min\":\"10.180.199.8\",\"ip_max\":\"10.180.199.8\",\"ports_str\":\"999\"}],\"domain_list\":[{\"domain_name\":\"any\"}]}");
            data.put("_action", 1);
            siteArray.add(data);
        }
        wafSiteRecord.put("waf_sites", siteArray);
        datumWriter.write(wafSiteRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();

    }


    /**
     * waf的默认http站点
     * @return
     */
    public GenericRecord getWafDefaultHttpSite(){
        Schema schema = getSchema();
        GenericRecord data = new GenericData.Record(schema.getField("waf_sites").schema().getElementType());
        data.put("id", 2147483647);
        data.put("name", "HTTP默认站点");
        data.put("site_type", 0);
        data.put("status", 1);
        data.put("policy", 1);
        data.put("ip_port_domain", "{\"ip_port\":[{\"ip_min\":\"0.0.0.0\",\"ip_max\":\"0.0.0.0\",\"ports_str\":\"0\"}],\"domain_list\":[{\"domain_name\":\"any\"}]}");
        data.put("_action", 1);
        return data;
    }
    /**
     * waf的默认https站点
     * @return
     */
    public GenericRecord getWafDefaultHttpsSite(){
        Schema schema = getSchema();
        GenericRecord data = new GenericData.Record(schema.getField("waf_sites").schema().getElementType());
        data.put("id", 2147483646);
        data.put("name", "HTTPS默认站点");
        data.put("site_type", 0);
        data.put("status", 1);
        data.put("policy", 1);
        data.put("ip_port_domain", "{\"ip_port\":[{\"ip_min\":\"0.0.0.0\",\"ip_max\":\"0.0.0.0\",\"ports_str\":\"0\"}],\"domain_list\":[{\"domain_name\":\"any\"}]}");
        data.put("_action", 1);
        return data;
    }






}
