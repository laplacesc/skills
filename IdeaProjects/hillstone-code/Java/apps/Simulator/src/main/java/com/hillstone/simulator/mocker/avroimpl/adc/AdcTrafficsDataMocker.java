package com.hillstone.simulator.mocker.avroimpl.adc;


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


public  class AdcTrafficsDataMocker extends IAvroMocker {

    private static final String virtual_servers_traffics = "virtual_servers_traffics";

    public AdcTrafficsDataMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.SERVER_LOADBALANCE_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.VIRTUAL_SERVERS_TRAFFICS_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.ADC_TRAFFICS_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.ADC_TRAFFICS_MD5;
    }

    @Override
    public Integer getTaskInterval(){
        return 60;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {

        Schema schema = getSchema();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        GenericRecord adcRecord = new GenericData.Record(schema);

        /**
         * 虚拟系统数量，不要改
         */
        int virtualSystemsCount = 1;
        /**
         * 虚拟系统数量 可配置 1-10000
         */
        int eachSystemHasServerCount = 100;
        GenericArray<GenericRecord> virtualSystems = new GenericData.Array<>(0, schema.getField(virtual_servers_traffics).schema());
        for (int i = 0; i < virtualSystemsCount; i++ ){
            GenericRecord virtualSystem = new GenericData.Record(schema.getField(virtual_servers_traffics).schema().getElementType());
            virtualSystem.put("vsys_id", i);
            virtualSystem.put("vsys", "虚拟系统" + i);
            GenericArray<GenericRecord> virtualServers = new GenericData.Array<>(0, schema.getField(virtual_servers_traffics).schema().getElementType().getField("virtual_servers_stats").schema());
            for (int j = 1; j < (eachSystemHasServerCount + 1); j++){
                GenericRecord virtualServer = new GenericData.Record(schema.getField(virtual_servers_traffics).schema().getElementType().getField("virtual_servers_stats").schema().getElementType());
                //这里的id要跟 AdcManageDataMocker 里面的id对应
                virtualServer.put("id", j);
                virtualServer.put("connection", 10000L);
                virtualServer.put("cps", 10000L);
                virtualServer.put("ibytes", 10000L);
                virtualServer.put("obytes", 10000L);
                virtualServer.put("ipackets", 10000L);
                virtualServer.put("opackets", 10000L);
                virtualServer.put("ibandwidth", 10000L);
                virtualServer.put("obandwidth", 10000L);
                virtualServer.put("running_status", 1);
                virtualServers.add(virtualServer);
            }
            virtualSystem.put("virtual_servers_stats", virtualServers);
            virtualSystems.add(virtualSystem);
        }
        adcRecord.put("virtual_servers_traffics", virtualSystems);

        datumWriter.write(adcRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }









}
