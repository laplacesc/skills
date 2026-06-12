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
 * @date create in 11:25 2024/01/25
 * @description
 */
public class IotReportV3Mocker extends IAvroMocker {
    public IotReportV3Mocker() {
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
        iotRecord.put("device_id", "99");
        iotRecord.put("ip", "192.168.4.11");
        iotRecord.put("mac", "02:1A:C5:02:00:00");
        iotRecord.put("vsys_id", 0);
        iotRecord.put("mfr", 1);
        iotRecord.put("type", 1);
        iotRecord.put("model", "WeMoSwitch");
        iotRecord.put("status", 0);
        iotRecord.put("trust", 1);
        iotRecord.put("district", "");
        iotRecord.put("time", 1700543625734L);
        iotRecord.put("rx", 0);
        iotRecord.put("tx", 0);
        datumWriter.write(iotRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "iot_monitor_report";
    }

    @Override
    public String getType() {
        return "iot_monitor";
    }

    @Override
    public String getAvroFileName() {
        return "b25d543970847a5ea2932e46ac299a79.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "b25d543970847a5ea2932e46ac299a79";
    }
}
