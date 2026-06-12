package com.hillstone.simulator.service.single;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.constant.DeviceStatus;
import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.mocker.IFileMocker;
import com.hillstone.simulator.service.DeviceRegisterInterface;
import com.hillstone.simulator.service.UploadDataService;
import com.hillstone.simulator.service.config.DeviceConfigService;
import com.hillstone.simulator.task.AvroDataSendTask;
import com.hillstone.simulator.task.BasicMonitorRelaTimeSendTask;
import com.hillstone.simulator.task.FileDataSendTask;
import com.hillstone.simulator.utils.FileUtils;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * 设备注册服务
 *
 * @author: bohuachen
 * @date: 2023/6/16 9:31
 * @description: some desc
 */
@Slf4j
@Service
public class SingleDeviceRegisterService implements DeviceRegisterInterface {

    @Autowired
    private SpringUtils springUtils;

    @Autowired
    private UploadDataService uploadDataService;

    @Autowired
    private DeviceInfoConfig deviceInfoConfig;

    @Autowired
    private DeviceConfigService deviceConfigService;


    /**
     * 设备注册流程
     */
    public void deviceRegister() {
        helloProcess();
    }

    /**
     * 平台消息处理(配置相关转到)
     *
     * @param msg
     */
    public void messageProcess(String msg) {
        MessageObject mo = new MessageObject(msg);
        String type = mo.getType();
        switch (type) {
            case "hello":
                break;
            case "requestCapabilities":
                requestCapabilitiesProcess();
                break;
            case "requestSchemas":
                requestSchemasProcess(mo);
                break;
            case "ReqDeviceBasicInfo":
            case "ReqIfInfo":
            case "ReqZoneInfo":
            case "ReqIpsLibrary":
            case "ReqAvLibrary":
            case "ReqLicenseInfo":
            case "ReqAppLibrary":
            case "ReqAppInfo":
            case "ReqUrlLibrary":
            case "Received message":
            case "ReqUrlCategory":
                reqDeviceBasicInfoProcess(mo);
                break;
            case "statusFlag":
                registeredProcess();
                break;
            default:
                // 配置流程
                deviceConfigService.messageProcess(mo, null);
        }
    }

    /**
     * hello 流程
     */
    public void helloProcess() {
        StringBuilder helloMessage = new StringBuilder("<msg category=\"Register\" type=\"hello\">");
        String strIp = "10.180.134.255";
        helloMessage.append("<userName>").append(deviceInfoConfig.getUsername())
                .append("</userName><sn>").append(deviceInfoConfig.getSn())
                .append("</sn><version>Version 5.5</version><manageIp>")
                .append(strIp).append("</manageIp><neIp>").append(strIp).append("</neIp>");
        if (deviceInfoConfig.getProtocolVersion().equals("1.0")) {
            helloMessage.append("<protocol-version>1.0</protocol-version>");
        }

        if (deviceInfoConfig.getIsYdDevice()) {
            helloMessage.append("<oem_id>34</oem_id>");
        } else {
            helloMessage.append("<oem_id>6</oem_id>");
        }
        helloMessage.append("</msg>");
        log.info(helloMessage.toString());
        springUtils.getBean(WebSocketClient.class).send(helloMessage.toString());
    }

    /**
     * requestCapabilities 流程
     */
    public void requestCapabilitiesProcess() {
        StringBuilder retCapabilities = new StringBuilder("<msg category=\"Register\" type=\"requestCapabilities\" result=\"ok\"> <all>");
        retCapabilities.append("<capability category=\"cloud_platform_configuration\" version=\"1\"/>");
        List<IAvroMocker> avroMockerList = deviceInfoConfig.getAllAvroDataMockers();
        if (!CollectionUtils.isEmpty(avroMockerList)) {
            for (IAvroMocker mocker : deviceInfoConfig.getAllAvroDataMockers()) {
                if (deviceInfoConfig.getDataUploadVersion().equals("1.1")) {
                    retCapabilities.append("<capability category=\"").append(mocker.getCategory()).append("\" type=\"").append(mocker.getType()).append("\" interval=\"").append(mocker.getTaskInterval()).append("\" schemaVersion=\"").append(mocker.getAvroMd5()).append("\" version=\"").append(deviceInfoConfig.getDataUploadVersion()).append("\"/>");
                } else {
                    retCapabilities.append("<capability category=\"").append(mocker.getCategory()).append("\" type=\"").append(mocker.getType()).append("\" interval=\"").append(mocker.getTaskInterval()).append("\" schemaVersion=\"").append(mocker.getAvroMd5()).append("\"/>");

                }
            }
        }

        switch (deviceInfoConfig.getProduct()) {
            case "ADC":
                retCapabilities.append("<capability category=\"server_loadbalance\" type=\"virtual_servers_management\" version=\"2\"/>");
                break;
            case "IPS":
                retCapabilities.append("<capability category=\"emergency\" type=\"critical_asset\" version=\"1\"/>");
                break;
            case "WAF":
                retCapabilities.append("<capability category=\"emergency\" type=\"waf\" version=\"1.0.1\"/>");
                break;
            case "FW":
                // 适配目前仅有FW设备支持AI bot的场景，后续扩展到其他产品时再下移 -- by cylv
                retCapabilities.append("<capability category=\"ai_bot\" version=\"1\"/>");
                break;
            default:
                break;
        }
        retCapabilities.append("<capability category=\"configuration_sequence\" version=\"1\"/>");
        retCapabilities.append("<capability category=\"library_update\" version=\"1\"/>");
        retCapabilities.append("</all> </msg>");
        log.info(retCapabilities.toString());
        springUtils.getBean(WebSocketClient.class).send(retCapabilities.toString());
    }

    /**
     * requestSchemas 流程
     *
     * @param mo
     */
    public void requestSchemasProcess(MessageObject mo) {
        if (!CollectionUtils.isEmpty(mo.getElements())) {
            for (Object item : mo.getElements().get(0).elements()) {
                Element e = (Element) item;
                String ver = e.attribute("version").getValue();
                log.info(ver);
                try {
                    // 上送schema
                    uploadDataService.uploadSchemas(ver, deviceInfoConfig);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        springUtils.getBean(WebSocketClient.class).send(AvroConstant.RET_SCHEMAS);
    }


    /**
     * reqDeviceBasicInfo 流程
     *
     * @param mo
     */
    public void reqDeviceBasicInfoProcess(MessageObject mo) {
        if ("ok".equalsIgnoreCase(mo.getResult())) {
            return;
        }
        String filename = mo.getType();
        try {
            String deviceBasicInfoDirectoryName = "/basicxml/deviceBasicInfo/" + deviceInfoConfig.getBasicInfoVersion() + "/";
            if (deviceInfoConfig.getProtocolVersion().equals("1.0")) {
                deviceBasicInfoDirectoryName = "/basicxml/deviceBasicInfo/v1/";
            }
            URL fileURL = this.getClass().getResource(deviceBasicInfoDirectoryName + filename + ".xml");
            if (fileURL == null) {
                log.info(filename + ".xml file is missing");
                return;
            }
            InputStream ins1 = this.getClass().getResourceAsStream(deviceBasicInfoDirectoryName + filename + ".xml");
            String content = deviceInfoConfig.getFilenameContent().get(filename);
            if (content == null) {
                content = FileUtils.replaceDeviceInfo(ins1);
                deviceInfoConfig.getFilenameContent().put(filename, content);
            }
            content = FileUtils.replaceDeviceInfo(content, deviceInfoConfig);
            log.info(content);
            springUtils.getBean(WebSocketClient.class).getConnection().send(content);
        } catch (IOException e) {
            log.error("read " + filename + ".xml error");
        }
    }

    /**
     * 注册后流程
     */
    public void registeredProcess() {
        log.info("the firewall {} register into the icloud platform", deviceInfoConfig.getSn());
        deviceInfoConfig.setDeviceStatus(DeviceStatus.REGISTERED);
        String content = "";
        switch (deviceInfoConfig.getProduct()) {
            case "ADC":
                content += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<msg category=\"server_loadbalance\" type=\"channel_switch\" result=\"on\">\n</msg>";
                springUtils.getBean(WebSocketClient.class).send(content);
                break;
            case "IPS":
                content += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<msg category=\"critical_asset\" type=\"critical_asset_switch\" result=\"on\">\n</msg>";
                springUtils.getBean(WebSocketClient.class).send(content);
                break;
            case "WAF":
                content += "<?xml version=\"1.0\"?>\n" +
                        "<msg category=\"waf_site\" type=\"waf_site_switch\" result=\"on\"/>";
                springUtils.getBean(WebSocketClient.class).send(content);
                break;
            default:
                break;
        }
        new BasicMonitorRelaTimeSendTask(0, 120, deviceInfoConfig, "BasicMonitorRelaTimeSendTask").start();
        List<IAvroMocker> avroMockerList = deviceInfoConfig.getAllAvroDataMockers();
        if (!CollectionUtils.isEmpty(avroMockerList)) {
            avroMockerList.forEach(avroMocker -> new AvroDataSendTask(0, avroMocker.getTaskInterval(), deviceInfoConfig, avroMocker.getTaskName(), avroMocker).start());
        }
        List<IFileMocker> fileMockerList = deviceInfoConfig.getAllFileDataMockers();
        if (!CollectionUtils.isEmpty(fileMockerList)) {
            fileMockerList.forEach(fileMocker -> new FileDataSendTask(0, fileMocker.getTaskInterval(), deviceInfoConfig, fileMocker.getTaskName(), fileMocker).start());
        }
        // 打开configuration开关
        springUtils.getBean(WebSocketClient.class).send("<msg category=\"cloud_platform_configuration\" type=\"cloud_platform_configuration_switch\" result=\"on\"></msg>");
        // Config @rtzhang
    }

}
