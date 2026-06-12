package com.hillstone.simulator.mocker.avroimpl.ips;


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


public  class IpsCaTrafficsDataMocker extends IAvroMocker {


    public IpsCaTrafficsDataMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.CRITICAL_ASSET_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.CRITICAL_ASSET_TRAFFICS_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.CRITICAL_ASSET_TRAFFICS_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.CRITICAL_ASSET_TRAFFICS_MD5;
    }

    @Override
    public Integer getTaskInterval(){
        return 60;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        //核心资产数量 <=128
        int caNum = 100;
        Schema schema = getSchema();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord assetsTrafficsRecord = new GenericData.Record(schema);

        GenericArray<GenericRecord> vsysArray = new GenericData.Array<GenericRecord>(1, schema.getField("critical_asset_traffics").schema());
        GenericRecord vsysData = new GenericData.Record(schema.getField("critical_asset_traffics").schema().getElementType());
        vsysData.put("vsys_id",0);

        GenericArray<GenericRecord> assetArray = new GenericData.Array<GenericRecord>(0, schema.getField("critical_asset_traffics").schema().getElementType().getField("critical_asset_stats").schema());

        for (int i=0; i < caNum; i++) {
            GenericRecord data = new GenericData.Record(schema.getField("critical_asset_traffics").schema().getElementType().getField("critical_asset_stats").schema().getElementType());
            data.put("id", i);
            data.put("connection", i* 10L);
            data.put("ibandwidth", i*11L);
            data.put("obandwidth", i*12L);
            assetArray.add(data);
        }

        vsysData.put("critical_asset_stats",assetArray);
        vsysArray.add(vsysData);


        assetsTrafficsRecord.put("critical_asset_traffics", vsysArray);
        datumWriter.write(assetsTrafficsRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }









}
