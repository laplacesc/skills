package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
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
public class AvStatusMocker extends IAvroMocker {


    private static final String AV_SCAN_ALGOR= "av_scan_algor";


    public AvStatusMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.AV_STATUS_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.AV_STATUS_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.AV_STATUS_AVRO_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.AV_STATUS_MD5;
    }

    @Override
    public Integer getTaskInterval(){
        return 86400;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord avStatusRecord = new GenericData.Record(schema);
        GenericArray<GenericRecord> avScanAlgorArray = new GenericData.Array<>(10, schema.getField(AV_SCAN_ALGOR).schema());

        avStatusRecord.put("device_sn", "1234");
        avStatusRecord.put("license_expire_time", "1234");
        avStatusRecord.put("feature_status", 12);
        avStatusRecord.put("database_version", "1.0");
        avStatusRecord.put("malware_number", 12);
        avStatusRecord.put("malware_mem_size", 12);
        avStatusRecord.put("ant_signature_name_mem_size", 12);
        avStatusRecord.put("malicious_url_number", 12);
        avStatusRecord.put("malicious_url_number", 12);
        avStatusRecord.put("malicious_url_mem_size", 12);
        avStatusRecord.put("ant_url_name_mem_size", 12);
        avStatusRecord.put("flash_memory_size", 12);

        for (int i = 0; i < 10; i++) {
            GenericRecord avStatusAvScanAlgor = new GenericData.Record(schema.getField(AV_SCAN_ALGOR).schema().getElementType());
            avStatusAvScanAlgor.put("type", i);
            avStatusAvScanAlgor.put("sig_type",  i + 10);
            avStatusAvScanAlgor.put("using_mem", i + 100);
            avScanAlgorArray.add(avStatusAvScanAlgor);
        }
        avStatusRecord.put(AV_SCAN_ALGOR, avScanAlgorArray);
        datumWriter.write(avStatusRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

}
