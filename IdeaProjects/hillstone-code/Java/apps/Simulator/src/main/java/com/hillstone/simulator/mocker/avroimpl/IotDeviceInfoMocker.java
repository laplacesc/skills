package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * @author zlpan
 * @date 2023/7/7 16:12
 */
public class IotDeviceInfoMocker extends IAvroMocker {


    public IotDeviceInfoMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.IOT_MONITOR_REPORT_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.IOT_MONITOR_DEVICE_INFO_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.IOT_MONITOR_DEVICE_INFO_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.IOT_MONITOR_DEVICE_INFO_MD5;
    }

    @Override
    public Integer getTaskInterval(){
        return 10;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        for (int i = 0; i < 2; i++) {
            GenericData.Record infoEntry = new GenericData.Record(schema);
            infoEntry.put("device_id", "test");
            infoEntry.put("ip", "175.139.118.22");
            infoEntry.put("mac", "mac" + i);
            infoEntry.put("mfr", 22);
            infoEntry.put("type", 33);
            infoEntry.put("model", "A");
            infoEntry.put("sess_proto", 11);
            infoEntry.put("sess_proto_name", "http");
            infoEntry.put("sess_app_name", "app_name");
            infoEntry.put("sess_user_server_name", "user_server_name");
            infoEntry.put("payload_num", 1);
            try (FileInputStream stream = new FileInputStream("D:\\project\\CPC\\CPC_SIMULATOR_MAIN\\cpc-simulator\\src\\main\\resources\\avro\\iot_monitor_report\\iot_device_info\\Setup-C-1-STA.pcap")) {
                byte[] bytes = IOUtils.toByteArray(stream);
                infoEntry.put("payloads_list", Collections.singleton(ByteBuffer.wrap(bytes)));
            }
            datumWriter.write(infoEntry, encoder);
        }
        encoder.flush();
        output.close();
        return output.toByteArray();
    }



}
