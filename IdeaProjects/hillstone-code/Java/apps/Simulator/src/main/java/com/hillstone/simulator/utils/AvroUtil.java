package com.hillstone.simulator.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rtzhang
 * @date 2023/9/27 14:38
 * @description 序列化、反序列化avro用
 */
public class AvroUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvroUtil.class);

    private AvroUtil() {
    }


    /**
     * 反序列化使用输入的schema，并非是class中的 schema
     * 输入class<T>的原因是指明反序列化的类
     * <p>
     * 该方法允许使用不对应的schema与class，请尽量在schema中设置default
     * 若schema中多字段，则不会反序列化到结果中
     * 若schema中少字段，则会按照生成class<T>的schema中的default
     * 若两者相同字段类型不同，出错
     *
     * @param schema
     * @param data
     * @return
     */
    public static <T> List<T> deSerializeAvroToObject(Schema schema, byte[] data, Class<T> type) {
        //读取器，这里不仅指定了序列化类型还指定了schema,所以有下一行
        SpecificDatumReader<T> specificDatumReader = new SpecificDatumReader<>(type);
        //由于schema可能有变更，比如新增字段，需要另外设置schema
        specificDatumReader.setSchema(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        List<T> genericRecordList = new ArrayList<>();
        try {
            while (!decoder.isEnd()) {
                genericRecordList.add(specificDatumReader.read(null, decoder));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return genericRecordList;
    }

    /**
     * 解析avro数据
     *
     * @param schema 解析avro需要的schema模式
     * @param data   avro的数据
     * @return 反序列化后的
     */
    public static List<GenericRecord> deSerializeAvroNoHeader(Schema schema, byte[] data) {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        List<GenericRecord> genericRecordList = new ArrayList<>();
        try {
            while (!decoder.isEnd()) {
                genericRecordList.add(datumReader.read(null, decoder));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return genericRecordList;
    }

    public static <T extends GenericRecord> byte[] serializeAvroNoHeaderByObject(Schema schema, List<T> genericRecordList) throws IOException {
        SpecificDatumWriter<T> userDatumWriter = new SpecificDatumWriter<>(schema);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        for (T genericRecord :
                genericRecordList) {
            userDatumWriter.write(genericRecord, binaryEncoder);
        }
        return outputStream.toByteArray();
    }
}
