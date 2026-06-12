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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:51
 * @description: some desc
 */
public class LogEventReportMocker extends IAvroMocker {


    public LogEventReportMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.LOG_EVENT_REPORT_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.LOG_EVENT_REPORT_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.LOG_EVENT_REPORT_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.LOG_EVENT_REPORT_MD5;
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
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord adReportRecord = new GenericData.Record(schema);
        adReportRecord.put("timestamp", 1);
        adReportRecord.put("syslog_id", 1);
        adReportRecord.put("type", 1);
        adReportRecord.put("severity", 1);
        adReportRecord.put("module", "module" + 1);
        adReportRecord.put("vsysname", "vsysname" + 1);
        adReportRecord.put("log_msg", "log_msg" + 1);
        datumWriter.write(adReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

}
