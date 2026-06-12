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

/**
 * adc的虚拟服务器，对应一键断网功能
 */
public  class IpsCaManageDataMocker extends IAvroMocker {


    public IpsCaManageDataMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.CRITICAL_ASSET_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.CRITICAL_ASSET_MANAGEMENT_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.CRITICAL_ASSET_MANAGEMENT_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.CRITICAL_ASSET_MANAGEMENT_MD5;
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
        GenericRecord assetsManagementRecord = new GenericData.Record(schema);
        assetsManagementRecord.put("_capacity", 1);
        GenericArray<GenericRecord> vsysArray = new GenericData.Array<GenericRecord>(1, schema.getField("report").schema());
        GenericRecord vsysData = new GenericData.Record(schema.getField("report").schema().getElementType());
        vsysData.put("vsys_id",0);
        vsysData.put("vsys_name","root");


        GenericArray<GenericRecord> assets = new GenericData.Array<GenericRecord>(0, schema.getField("report").schema().getElementType().getField("critical_asset").schema());
        for (int i = 0; i < caNum; i++) {
            GenericRecord assetData = new GenericData.Record(schema.getField("report").schema().getElementType().getField("critical_asset").schema().getElementType());
            assetData.put("id",i);
            assetData.put("name","name"+i);
            assetData.put("zone","zone"+i);
            assetData.put("ip",(long)i);
            assetData.put("desc","desc"+i);
            assetData.put("status",1);
            assetData.put("_action",1);
            assets.add(assetData);
        }
        vsysData.put("critical_asset",assets);
        vsysArray.add(vsysData);
        assetsManagementRecord.put("report", vsysArray);
        datumWriter.write(assetsManagementRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }









}
