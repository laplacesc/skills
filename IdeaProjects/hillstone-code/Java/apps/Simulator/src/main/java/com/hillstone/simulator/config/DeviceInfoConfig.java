package com.hillstone.simulator.config;

import com.hillstone.simulator.constant.DeviceStatus;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.mocker.IFileMocker;
import com.hillstone.simulator.mocker.avroimpl.CloudQueryApiKeyRequestReportMocker;
import com.hillstone.simulator.utils.SeriesUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 设备连接信息基础类
 *
 * @author: bohuachen
 * @date: 2023/6/16 8:44
 * @description:
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "device.info.client")
public class DeviceInfoConfig {

    private String wsUrl;

    private String sn;

    private String username;

    private String httpUrl;

    private String product;

    private String fwName;

    private String platform;

    private String protocolVersion;

    private String bootFile;

    private String realTimeMonitorVersion;

    private String basicInfoVersion;

    private Boolean isYdDevice;

    private String dataUploadVersion;

    private Integer processModel;

    private DeviceStatus deviceStatus = DeviceStatus.UNSTARTED;

    private List<IAvroMocker> allAvroDataMockers;

    private List<IFileMocker> allFileDataMockers;

    private Map<String, String> filenameContent = new ConcurrentHashMap<>();

    private ConcurrentMap<String, IAvroMocker> md5Mockers = new ConcurrentHashMap<>();


    public List<IAvroMocker> getAllAvroDataMockers() {
        if (allAvroDataMockers == null) {
            setAllAvroDataMockers();
        }
        return allAvroDataMockers;
    }

    public void setAllAvroDataMockers() {

        allAvroDataMockers = new ArrayList<>();
        try {
            // 添加需要的mocker
            //allAvroDataMockers.add(new ThreatEventMocker());
//ADC
            //allAvroDataMockers.add(new AdcManageDataMocker());
//            allAvroDataMockers.add(new AdcTrafficsDataMocker());
//IPS
//            allAvroDataMockers.add(new IpsCaManageDataMocker());
//            allAvroDataMockers.add(new IpsCaTrafficsDataMocker());
//WAF
//            allAvroDataMockers.add(new WafSiteDataMocker());

            //allAvroDataMockers.add(new AppSigReportsMocker());

            allAvroDataMockers.add(new CloudQueryApiKeyRequestReportMocker());
            // allAvroDataMockers.add(new BotnetC2BlackReportMocker());

            allAvroDataMockers.forEach(iAvroMocker -> {
                md5Mockers.put(iAvroMocker.getAvroMd5(), iAvroMocker);
            });


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public List<IFileMocker> getAllFileDataMockers() {
        if (allFileDataMockers == null) {
            setAllFileDataMockers();
        }
        return allFileDataMockers;
    }

    public void setAllFileDataMockers() {

        allFileDataMockers = new ArrayList<>();
        try {
            // 添加需要的mocker
//            allFileDataMockers.add(new ThreatEventMocker());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getProduct() {
        return SeriesUtils.getPlatformSeries(this.getPlatform()).getSeries();
    }


    public DeviceInfoConfig deepCopy() {
        DeviceInfoConfig deviceInfoConfig = new DeviceInfoConfig();
        deviceInfoConfig.setWsUrl(this.getWsUrl());
        deviceInfoConfig.setSn(this.getSn());
        deviceInfoConfig.setUsername(this.getUsername());
        deviceInfoConfig.setHttpUrl(this.getHttpUrl());
        deviceInfoConfig.setProduct(this.getProduct());
        deviceInfoConfig.setFwName(this.getFwName());
        deviceInfoConfig.setPlatform(this.getPlatform());
        deviceInfoConfig.setProtocolVersion(this.getProtocolVersion());
        deviceInfoConfig.setBootFile(this.getBootFile());
        deviceInfoConfig.setRealTimeMonitorVersion(this.getRealTimeMonitorVersion());
        deviceInfoConfig.setBasicInfoVersion(this.getBasicInfoVersion());
        deviceInfoConfig.setIsYdDevice(this.isYdDevice);
        deviceInfoConfig.setDataUploadVersion(this.getDataUploadVersion());
        deviceInfoConfig.setDeviceStatus(this.getDeviceStatus());
        deviceInfoConfig.setAllAvroDataMockers(this.getAllAvroDataMockers());
        deviceInfoConfig.setAllFileDataMockers(this.getAllFileDataMockers());
        deviceInfoConfig.setFilenameContent(this.getFilenameContent());
        deviceInfoConfig.setMd5Mockers(this.getMd5Mockers());
        return deviceInfoConfig;
    }
}
