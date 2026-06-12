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

/**
 * @author bhliu
 * @date create in 10:51 2024/01/25
 * @description
 */
public class IotDeviceInfoV3Mocker extends IAvroMocker {
    public IotDeviceInfoV3Mocker() {
        super();
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        Schema schema;
        try (InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath())) {
            schema = new Schema.Parser().parse(ins);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord iotRecord = new GenericData.Record(schema);
        iotRecord.put("format_ver", "");
        iotRecord.put("rule_engine_ver", "");
        iotRecord.put("algo_engine_ver", "");
        iotRecord.put("device_id", "99");
        iotRecord.put("detect_method", 1);
        iotRecord.put("ip", "192.168.4.11");
        iotRecord.put("is_mac_referrible", 0);
        iotRecord.put("mac", "02:1A:C5:02:00:00");
        iotRecord.put("mfr", "Dahua");
        iotRecord.put("type", "WeMoSwitch");
        iotRecord.put("model", "WeMoSwitch");
        iotRecord.put("os_version", "Linux 3.2 - 3.8");
        iotRecord.put("os_family", "Linux");
        iotRecord.put("sess_proto", 1);
        iotRecord.put("sess_proto_name", "proto_name");
        iotRecord.put("sess_app_name", "app_name");
        iotRecord.put("sess_user_server_name", "server_name");
        iotRecord.put("payload_num", 1);
        iotRecord.put("payload_size", 1);
        iotRecord.put("payloads", "");
        datumWriter.write(iotRecord, encoder);
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
        return "iot_device_info";
    }

    @Override
    public String getAvroFileName() {
        return "f5ed627efadc5d1804639aaa3e52a2be.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "f5ed627efadc5d1804639aaa3e52a2be";
    }
}
