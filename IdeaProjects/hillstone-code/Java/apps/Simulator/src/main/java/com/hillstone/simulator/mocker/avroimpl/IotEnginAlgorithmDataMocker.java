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
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * @author zlpan
 * @date 2023/10/18 12:26
 */
public class IotEnginAlgorithmDataMocker extends IAvroMocker {
    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);

        Random random = new Random();


        GenericRecord iotAlgorithmEntry = new GenericData.Record(schema);

        iotAlgorithmEntry.put("device_id", "wasd");
        iotAlgorithmEntry.put("ip_addr", "1.1.1.2");
        iotAlgorithmEntry.put("mac", "mac");
        iotAlgorithmEntry.put("vr_vs_id", 1);
        iotAlgorithmEntry.put("is_l2", 1);
        iotAlgorithmEntry.put("is_tap", 1);
        iotAlgorithmEntry.put("app_id", "6,15,60");
        iotAlgorithmEntry.put("cur_sess_num", random.nextInt(100));
        iotAlgorithmEntry.put("total_sess_num", 3);
        iotAlgorithmEntry.put("port", "5,66,449");
        iotAlgorithmEntry.put("protocol_num", 2);
        iotAlgorithmEntry.put("protocol", "0,1");
        iotAlgorithmEntry.put("proto_sess_num", random.nextInt(20));
        iotAlgorithmEntry.put("proto_payload_len", random.nextInt(50));
//        byte[] bytes = {1, 2, 3, 4};
//        iotAlgorithmEntry.put("payloads_list", ByteBuffer.wrap(bytes));
        try (FileInputStream inputStream = new FileInputStream("D:\\project\\CPC\\CPC_SIMULATOR_MAIN\\cpc-simulator\\src\\main\\resources\\avro\\iot_monitor_report\\iot_device_info\\Setup-C-1-STA.pcap")) {
            byte[] byteArray = IOUtils.toByteArray(inputStream);
            iotAlgorithmEntry.put("payloads_list", ByteBuffer.wrap(byteArray));
        } catch (IOException e) {

        }

        datumWriter.write(iotAlgorithmEntry, encoder);


        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "iot_monitor_icloud_engine";
    }

    @Override
    public String getType() {
        return "device_algorithm_info_upload";
    }

    @Override
    public String getAvroFileName() {
        return "336465bbb9328abb2d04569604ff0efc.avsc";
    }


    @Override
    public String getAvroMd5() {
        return "336465bbb9328abb2d04569604ff0efc";
    }

    @Override
    public Integer getTaskInterval() {
        return 1;
    }
    
}
