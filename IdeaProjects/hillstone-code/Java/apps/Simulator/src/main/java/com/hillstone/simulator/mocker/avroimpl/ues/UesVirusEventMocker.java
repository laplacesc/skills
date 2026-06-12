package com.hillstone.simulator.mocker.avroimpl.ues;

import com.hillstone.simulator.config.DeviceInfoConfig;
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
 * @date: 2024/1/24 5:51
 * @description: some desc
 */
public class UesVirusEventMocker extends IAvroMocker {

    public UesVirusEventMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "ues";
    }

    @Override
    public String getType() {
        return "virus_event";
    }

    @Override
    public String getAvroFileName() {
        return "4a5b22db6511618bde9a7e3a01c6a639.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "4a5b22db6511618bde9a7e3a01c6a639";
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        GenericRecord record = new GenericData.Record(schema);

        GenericArray<String> fileNameList = new GenericData.Array<>(10,
                schema.getField("fileName").schema());
        fileNameList.add("C:\\\\Users\\\\test111\\\\Desktop\\\\type\\\\Email-Worm.VBS.HappyTime.A.vbs");
        fileNameList.add("C:\\\\Users\\\\test111\\\\Desktop\\\\type\\\\geliqi\\\\Email-Worm.VBS.HappyTime.A.vbs");

        record.put("fileName", fileNameList);
        record.put("fileSize", 9899L);
        record.put("fileType", ".vbs");

        GenericArray<String> fileRealNameList = new GenericData.Array<>(10,
                schema.getField("fileRealName").schema());
        fileRealNameList.add("Email-Worm.VBS.HappyTime.A.vbs");

        record.put("fileRealName", fileRealNameList);
        record.put("threatType", "virus");
        record.put("threatName", "VBS.Happytime.G");
        record.put("md5", "824a05957d9ba7be017fb8a0f80af5b2");
        record.put("hashSha1", "73d5cf63d27885f4f71354b0eca0284b45efb6e4");
        record.put("hashSha256", "a17e1f387de381232ac6e5864035965b2c296d2f3dbf05c0e19d899c7b15db06");
        record.put("hashSha512", "e62cc4e07842c5030dd38ece5ea921191682579b6d6104acaa98ba25b7264d5c72436d9cd8c7947d09f9527c58306b4b487deafa34fd9b9a4fa9ea1240f5fb60");
        record.put("crc32", "a9776663");
        record.put("ssdeep", "");
        record.put("vhash", "");

        GenericArray<String> tags = new GenericData.Array<>(10,
                schema.getField("tags").schema());
        tags.add("virus");

        record.put("tags", tags);

        record.put("engine", 1);


        datumWriter.write(record, encoder);
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
