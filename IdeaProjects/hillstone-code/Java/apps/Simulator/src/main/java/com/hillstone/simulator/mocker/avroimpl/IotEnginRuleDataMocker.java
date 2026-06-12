package com.hillstone.simulator.mocker.avroimpl;


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
import java.util.Random;

public class IotEnginRuleDataMocker extends IAvroMocker {


    public IotEnginRuleDataMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "iot_monitor_icloud_engine";
    }

    @Override
    public String getType() {
        return "device_rule_info_upload";
    }

    @Override
    public String getAvroFileName() {
        return "af15d5fdacd5fdfea300e88a8e253e82.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "af15d5fdacd5fdfea300e88a8e253e82";
    }

    @Override
    public Integer getTaskInterval() {
        return 1;
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);

        Random random = new Random();

        for (int i = 0; i < 2; i++) {
            GenericRecord iotAlgorithmEntry = new GenericData.Record(schema);

            iotAlgorithmEntry.put("device_id", i + "");
            iotAlgorithmEntry.put("ip_addr", "1.1.1." + (i + 1));
            iotAlgorithmEntry.put("mac", "mac" + i);
            iotAlgorithmEntry.put("vr_vs_id", random.nextInt(100000));
            iotAlgorithmEntry.put("is_l2", 1);
            iotAlgorithmEntry.put("is_tap", 1);
            iotAlgorithmEntry.put("app_id", "6,15,60");
            iotAlgorithmEntry.put("cur_sess_num", 1);
            iotAlgorithmEntry.put("total_sess_num", 3);
            iotAlgorithmEntry.put("port", "5,66,449");
            iotAlgorithmEntry.put("protocol_num", 2);
            iotAlgorithmEntry.put("protocol", "0,1");
            iotAlgorithmEntry.put("proto_sess_num", 1);
            iotAlgorithmEntry.put("proto_payload_len", 1);

            GenericData.Array<GenericRecord> protocols = new GenericData.Array<>(2, schema.getField("protocols").schema());
            GenericData.Record protocol1 = new GenericData.Record(schema.getField("protocols").schema().getElementType());
            protocol1.put("protocol_id", 8);
            protocol1.put("protocol_len", 10);
            protocol1.put("protocol_content", "<option55>22222222</option55>");
            protocols.add(protocol1);

            GenericData.Record protocol2 = new GenericData.Record(schema.getField("protocols").schema().getElementType());
            protocol2.put("protocol_id", 10);
            protocol2.put("protocol_len", 20);
            protocol2.put("protocol_content", "<host>dddd</host><user_agent>Mozilla/5.0</user_agent>");
            protocols.add(protocol2);

            iotAlgorithmEntry.put("protocols", protocols);

            datumWriter.write(iotAlgorithmEntry, encoder);
        }
        encoder.flush();
        output.close();
        return output.toByteArray();
    }
}
