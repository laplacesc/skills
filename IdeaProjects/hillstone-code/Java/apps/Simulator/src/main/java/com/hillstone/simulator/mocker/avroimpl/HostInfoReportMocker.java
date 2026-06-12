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
 * @date create in 17:55 2024/01/24
 * @description
 */
public class HostInfoReportMocker extends IAvroMocker {
    public HostInfoReportMocker() {
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
        hostReportRecord.put("id", 320);
        hostReportRecord.put("hostname", "");
        hostReportRecord.put("sip", 3232238342L);
        hostReportRecord.put("zone", "tap-bds");
        hostReportRecord.put("osid", 5);
        hostReportRecord.put("browserid", 2);
        hostReportRecord.put("type", 0);
        hostReportRecord.put("status", 1);
        hostReportRecord.put("updata_time", 1700471815);
        hostReportRecord.put("username", "");
        hostReportRecord.put("a3name", "");
        hostReportRecord.put("vsys", "root");
        hostReportRecord.put("vr", "trust-vr");
        hostReportRecord.put("mac", "00:00:00:00:00:00");
        hostReportRecord.put("last_update", 1700560805);
        hostReportRecord.put("os_change_time", 1700471934);

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
        return "host_info_report";
    }

    @Override
    public String getAvroFileName() {
        return "100bdff1af70432518a054a77e260589.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "100bdff1af70432518a054a77e260589";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }
}
