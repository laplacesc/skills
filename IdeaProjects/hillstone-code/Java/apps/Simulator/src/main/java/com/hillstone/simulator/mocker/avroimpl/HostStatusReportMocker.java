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
 * @date create in 9:25 2024/01/25
 * @description
 */
public class HostStatusReportMocker extends IAvroMocker {
    public HostStatusReportMocker() {
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
        GenericRecord hostReportRecord = new GenericData.Record(schema);
        hostReportRecord.put("ip", 169684491);
        hostReportRecord.put("vsys", "root");
        hostReportRecord.put("vr", "trust-vr");
        hostReportRecord.put("severity", 1200);
        hostReportRecord.put("confidence", 24);
        hostReportRecord.put("status", 1);
        datumWriter.write(hostReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "host";
    }

    @Override
    public String getType() {
        return "host_status_report";
    }

    @Override
    public String getAvroFileName() {
        return "ee97571f93760a184b96d3df85b1aa84.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "ee97571f93760a184b96d3df85b1aa84";
    }
}
