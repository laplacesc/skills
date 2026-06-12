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

/**
 * adc的虚拟服务器，对应一键断网功能
 */
public  class AdcManageDataMocker extends IAvroMocker {

    private static final String virtual_server_report = "virtual_server_report";

    public AdcManageDataMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.SERVER_LOADBALANCE_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.VIRTUAL_SERVERS_MANAGEMENT_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.ADC_MANAGE_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.ADC_MANAGE_MD5;
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
        int eachSystemHasServerCount = 101;
        GenericArray<GenericRecord> virtualSystems = new GenericData.Array<>(0, schema.getField(virtual_server_report).schema());
        for (int i = 0; i < virtualSystemsCount; i++ ){
            GenericRecord virtualSystem = new GenericData.Record(schema.getField(virtual_server_report).schema().getElementType());
            virtualSystem.put("vsys_id", i);
            virtualSystem.put("vsys", "虚拟系统" + i);
            GenericArray<GenericRecord> virtualServers = new GenericData.Array<>(0, schema.getField(virtual_server_report).schema().getElementType().getField("virtual_servers").schema());
            for (int j = 1; j < (eachSystemHasServerCount + 1); j++){
                GenericRecord virtualServer = new GenericData.Record(schema.getField(virtual_server_report).schema().getElementType().getField("virtual_servers").schema().getElementType());
                //id 1- 65535
                virtualServer.put("id", j);
                //长度1-255
                virtualServer.put("name", "虚拟服务" + j);
                virtualServer.put("protocol", 1);
                virtualServer.put("status", 1);
                virtualServer.put("running_status", 1);
                virtualServer.put("_action", 1);
                //ip_port有时候会是一个json字符串，对应数据库里面的对应字段
                virtualServer.put("ip_port", "10.180.139." + j + ":9090");
                virtualServers.add(virtualServer);
            }
            virtualSystem.put("virtual_servers", virtualServers);
            virtualSystems.add(virtualSystem);
        }
        adcRecord.put("_capacity", 1);
        adcRecord.put("virtual_server_report", virtualSystems);

        datumWriter.write(adcRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }









}
