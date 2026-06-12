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
 * @date create in 13:37 2024/01/25
 * @description
 */
public class IotPropertyMocker extends IAvroMocker {
    public IotPropertyMocker() {
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
        iotRecord.put("deviceId", "99");
        iotRecord.put("ip", "192.168.4.11");
        iotRecord.put("mac", "02:1A:C5:02:00:00");
        iotRecord.put("type", "IPC");
        iotRecord.put("typeCn", "网络摄像机");
        iotRecord.put("mfr", "Dahua");
        iotRecord.put("mfrCn", "大华");
        iotRecord.put("model", "WeMoSwitch");
        iotRecord.put("osFamily", "Linux");
        iotRecord.put("osVersion", "Linux 3.2 - 3.8");
        iotRecord.put("district", "");
        iotRecord.put("lastUpdateTime", 14);
        iotRecord.put("status", 2);
        datumWriter.write(iotRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "iot_monitor_upload";
    }

    @Override
    public String getType() {
        return "iot_property";
    }

    @Override
    public String getAvroFileName() {
        return "06068539e54aee34771231c0476951bf.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "06068539e54aee34771231c0476951bf";
    }
}
