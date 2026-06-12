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
public class WafDefacementInfoMocker extends IAvroMocker {

    public WafDefacementInfoMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "waf_defacement";
    }

    @Override
    public String getType() {
        return "waf_defacement_info";
    }

    @Override
    public String getAvroFileName() {
        return "a4f408105893dd5a72ca86974df504fe.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "a4f408105893dd5a72ca86974df504fe";
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
        GenericRecord wafDEfacementRecord = new GenericData.Record(schema);
        wafDEfacementRecord.put("action", 1);

        GenericRecord data = new GenericData.Record(schema.getField("waf_defacement_info").schema());
        data.put("id", 1);
        data.put("site_name", "site1");
        data.put("path", "http://www.baidu.com/gaoji/preferences.html");
        data.put("time","2020-03-31 16:38:16");

        wafDEfacementRecord.put("waf_defacement_info", data);
//        System.out.println("上送篡改消息");
        datumWriter.write(wafDEfacementRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }
}
