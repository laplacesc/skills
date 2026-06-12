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
public class LogOperatMocker extends IAvroMocker {
    public LogOperatMocker() {
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
        GenericRecord logCfRecord = new GenericData.Record(schema);
        logCfRecord.put("timestamp", 1700532173);
        logCfRecord.put("syslog_id", 1179444763);
        logCfRecord.put("vsys_name", "root");
        logCfRecord.put("vsys_id", 0);
        logCfRecord.put("log_msg", "Content filter: IP 153.3.238.197 (-) VR trust-vr, http://mobile.baidu.com/item?pid=1530508&source=aladdin@wise_app13@info&ala=strong@5006828166@1530508@mobileapp.baidu.com@info@兴业银行&docid=5006828166&alaSid=&from=1022306o&ala=app_mobile_simple@5267@s@兴业银行@7585848955994690147@info&sid=, http, action PERMIT, reason with keyword, 重庆 (beta), rule beta-new, charset N/A, N/A\n");

        datumWriter.write(logCfRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "logd_operat";
    }

    @Override
    public String getType() {
        return "log_operat";
    }

    @Override
    public String getAvroFileName() {
        return "1b8f2b8181c13ed9e0ce2a69560e20c8.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "1b8f2b8181c13ed9e0ce2a69560e20c8";
    }
}

