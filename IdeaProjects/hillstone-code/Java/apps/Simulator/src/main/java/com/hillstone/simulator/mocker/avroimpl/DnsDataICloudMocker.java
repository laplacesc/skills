package com.hillstone.simulator.mocker.avroimpl;

import cn.hutool.core.io.FileUtil;
import com.google.common.io.ByteStreams;
import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.dns_data.dns_data_icloud.dns_data;
import com.hillstone.simulator.mocker.IAvroMocker;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:51
 * @description: 新版威胁事件
 */
@Slf4j
public class DnsDataICloudMocker extends IAvroMocker {

    public DnsDataICloudMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return "dns_data";
    }

    @Override
    public String getType() {
        return "dns_data_icloud";
    }

    @Override
    public String getAvroFileName() {
        return "e0492ff75a61afbd06c9f73b69644cd0.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "e0492ff75a61afbd06c9f73b69644cd0";
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {


        dns_data dnsData = new dns_data();
        dnsData.setDnsPcap(ByteBuffer.wrap(ByteStreams.toByteArray(FileUtil.getInputStream(AvroConstant.DEMO_FILE_PATH + "/dns.pcap"))));
        try {
            DatumWriter<dns_data> userDatumWriter = new SpecificDatumWriter<>(dns_data.class);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
            userDatumWriter.write(dnsData, binaryEncoder);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // 如果生成avro文件较为麻烦，也可以直接传入一个avro文件
        return new byte[0];
    }


    /**
     * 需要生成avro model 文件需要放入指定路径
     *
     * @return
     */
    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

}
