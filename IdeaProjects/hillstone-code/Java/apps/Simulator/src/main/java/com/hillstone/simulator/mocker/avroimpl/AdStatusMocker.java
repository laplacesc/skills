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
public class AdStatusMocker extends IAvroMocker {

    private static final String AD_STATUS= "ad_status";


    public AdStatusMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.AD_STATUS_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.AD_STATUS_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.AD_STATUS_AVRO_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.AD_STATUS_MD5;
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
        GenericRecord adReportRecord = new GenericData.Record(schema);
        GenericArray<GenericRecord> adStatusArray = new GenericData.Array<>(10, schema.getField(AD_STATUS).schema());
        for (int i = 0; i < 2; i++) {
            GenericRecord adIcloudAdStatus = new GenericData.Record(schema.getField(AD_STATUS).schema().getElementType());
            adIcloudAdStatus.put("zone_type", i + 1);
            adIcloudAdStatus.put("threat_type", "site" + (i + 1));
            adIcloudAdStatus.put("sub_threat_type", "site" + (i + 1));
            adIcloudAdStatus.put("match_counter", 1);
            adStatusArray.add(adIcloudAdStatus);
        }
        adReportRecord.put(AD_STATUS, adStatusArray);
        datumWriter.write(adReportRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

}
