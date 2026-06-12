package com.hillstone.simulator.mocker.avroimpl;

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
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * ips-buffer_rule-hit
 * <p>
 * 增加base64 encode标记
 *
 * @author cylv
 * @date create in 13:47 2024/05/16
 */
public class IpsRuleHitV3Mocker extends IAvroMocker {
    public IpsRuleHitV3Mocker() {
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
        GenericRecord ipsRecord = new GenericData.Record(schema);
        ipsRecord.put("signature_id", 2307392200L);
        ipsRecord.put("hit_time", 1);
        ipsRecord.put("idp_engine_ver", 735);
        ipsRecord.put("idp_sig_ver", "3.0.183");
        ipsRecord.put("is_base64", 1);


        GenericArray<GenericRecord> ipsBufferStats = new GenericData.Array<>(10, schema.getField("ips_buffer_stats").schema());
        GenericRecord genericRecord = new GenericData.Record(schema.getField("ips_buffer_stats").schema().getElementType());
        genericRecord.put("proto", "http");
        genericRecord.put("src_ip", "192.168.49.51");
        genericRecord.put("dst_ip", "121.199.31.240");
        genericRecord.put("src_port", 24095);
        genericRecord.put("dst_port", 24095);
        genericRecord.put("buffer", new String(Base64.getEncoder().encode("HTTP/1.1 206 Partial Content\r\nContent-Type: audio/mpeg\r\nLast-Modified: Wed, 06 Mar 2013 00:54:47 GMT\r\nAccept-Ranges: bytes\r\nETag: \"8065d03251ace1:0\"\r\nServer: Microsoft-IIS/7.5\r\nX-Powered-By: ASP.NET\r\nDate: Tue, 21 Nov 2023 01:58:24 GMT\r\nConnection: close\r\nContent-Length: 40090\r\nContent-Range: bytes 0-40089/40090\r\n\r\nID3\u0003".getBytes(StandardCharsets.UTF_8))));
        ipsBufferStats.add(genericRecord);
        ipsRecord.put("ips_buffer_stats", ipsBufferStats);

        datumWriter.write(ipsRecord, encoder);
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    @Override
    public String getCategory() {
        return "ips_buffer";
    }

    @Override
    public String getType() {
        return "rule_hit";
    }

    @Override
    public String getAvroFileName() {
        return "d46a19ceed57c7c3bc901fc17033d58e.avsc";
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }

    @Override
    public String getAvroMd5() {
        return "d46a19ceed57c7c3bc901fc17033d58e";
    }
}
