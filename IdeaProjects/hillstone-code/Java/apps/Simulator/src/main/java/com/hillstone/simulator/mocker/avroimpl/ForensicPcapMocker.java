package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.forensic.pcap.forensic_pcap;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.utils.AvroUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: myyang
 * @date: 2023/11/16 17:16
 * @description: some desc
 */
public class ForensicPcapMocker extends IAvroMocker {

    public ForensicPcapMocker() { super(); }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        long time = System.currentTimeMillis();
        List<forensic_pcap> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            forensic_pcap forensicPcap = new forensic_pcap();
            forensicPcap.setPcapId(i);
            forensicPcap.setTimestamp(time + i);
            forensicPcap.setContent("1MOyoQIABAAAAAAAAAAAAP//AAABAAAAaOksZVkVSiA8AAAAOwAAAAAcVIL35QAcVGAhxggARQAALZ56QAA8BsShCsAJPQq2vPxxVwgbCX2LSsTa9jlQGIowmZ0AAP/69v/wAA==");
            list.add(forensicPcap);
        }
        return AvroUtil.serializeAvroNoHeaderByObject(forensic_pcap.getClassSchema(), list);
    }

    @Override
    public String getCategory() {
        return AvroConstant.PCAP_INFO_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.PCAP_INFO_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.PCAP_INFO_FILENAME;
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.PCAP_INFO_MD5;
    }

    @Override
    public Integer getTaskInterval() {
        return 60;
    }


}
