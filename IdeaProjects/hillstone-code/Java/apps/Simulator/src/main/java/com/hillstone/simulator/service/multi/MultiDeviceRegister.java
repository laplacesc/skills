package com.hillstone.simulator.service.multi;

import com.hillstone.simulator.client.MultiDeviceWebSocketClient;
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
import com.hillstone.simulator.task.FileDataSendTask;
import com.hillstone.simulator.task.MultiMonitorTask;
import com.hillstone.simulator.utils.FileUtils;
import com.hillstone.simulator.utils.SpringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author rtzhang
 * @date 2023/10/31 11:09
 * @description 多线程下
 */
public class MultiDeviceRegister implements DeviceRegisterInterface {
    private static final Logger logger = LoggerFactory.getLogger(MultiDeviceRegister.class);

    private final MultiDeviceWebSocketClient deviceWebSocketClient;

    private final UploadDataService uploadDataService;

    private DeviceInfoConfig deviceInfoConfig;
    private MultiDeviceConfigModel multiDeviceConfigModel;


    public MultiDeviceRegister(MultiDeviceConfigModel multiDeviceConfigModel, DeviceInfoConfig deviceInfoConfig, UploadDataService uploadDataService) {
        this.multiDeviceConfigModel = multiDeviceConfigModel;
        this.uploadDataService = uploadDataService;
        this.deviceWebSocketClient = multiDeviceConfigModel.getMultiDeviceWebSocketClient();
        multiDeviceConfigModel.getMultiDeviceWebSocketClient().setRunnable(this);
        this.deviceInfoConfig = deviceInfoConfig.deepCopy();
        this.deviceInfoConfig.setSn(multiDeviceConfigModel.getSn());
        this.deviceInfoConfig.setUsername(multiDeviceConfigModel.getUserName());
    }

    public DeviceInfoConfig getDeviceInfoConfig() {
        return deviceInfoConfig;
    }

    public void setDeviceInfoConfig(DeviceInfoConfig deviceInfoConfig) {
        this.deviceInfoConfig = deviceInfoConfig;
    }

    @Override
    public void deviceRegister() {
        this.deviceWebSocketClient.connect();
    }

    @Override
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
                SpringUtils.getBean(DeviceConfigService.class).messageProcess(mo, multiDeviceConfigModel);
        }
    }

    @Override
    public void helloProcess() {
        StringBuilder helloMessage = new StringBuilder("<msg category=\"Register\" type=\"hello\">");
        String strIp = "10.180.134.25";
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
//        logger.info(helloMessage.toString());
        deviceWebSocketClient.send(helloMessage.toString());
    }

    @Override
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
        retCapabilities.append("</all> </msg>");
//        System.out.println(retCapabilities);
        deviceWebSocketClient.send(retCapabilities.toString());
    }

    @Override
    public void requestSchemasProcess(MessageObject mo) {
        if (!CollectionUtils.isEmpty(mo.getElements())) {
            for (Object item : mo.getElements().get(0).elements()) {
                Element e = (Element) item;
                String ver = e.attribute("version").getValue();
//                System.out.println(ver);
                try {
                    // 上送schema
                    uploadDataService.uploadSchemas(ver, deviceInfoConfig);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        deviceWebSocketClient.send(AvroConstant.RET_SCHEMAS);
    }

    @Override
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
                logger.error("{}.xml file is missing", filename);
                return;
            }
            InputStream ins1 = this.getClass().getResourceAsStream(deviceBasicInfoDirectoryName + filename + ".xml");
            String content = deviceInfoConfig.getFilenameContent().get(filename);
            if (content == null) {
                content = FileUtils.replaceDeviceInfo(ins1);
                deviceInfoConfig.getFilenameContent().put(filename, content);
            }
            content = FileUtils.replaceDeviceInfo(content, deviceInfoConfig);
//            logger.info(content);
            deviceWebSocketClient.send(content);
        } catch (IOException e) {
            logger.error("read " + filename + ".xml error");
        }
    }

    @Override
    public void registeredProcess() {
        logger.info("the firewall {} register into the icloud platform", deviceInfoConfig.getSn());
        deviceInfoConfig.setDeviceStatus(DeviceStatus.REGISTERED);
        String content = "";
        switch (deviceInfoConfig.getProduct()) {
            case "ADC":
                content += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<msg category=\"server_loadbalance\" type=\"channel_switch\" result=\"on\">\n</msg>";
                deviceWebSocketClient.send(content);
                break;
            case "IPS":
                content += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<msg category=\"critical_asset\" type=\"critical_asset_switch\" result=\"on\">\n</msg>";
                deviceWebSocketClient.send(content);
                break;
            case "WAF":
                content += "<?xml version=\"1.0\"?>\n" +
                        "<msg category=\"waf_site\" type=\"waf_site_switch\" result=\"on\"/>";
                deviceWebSocketClient.send(content);
                break;
            default:
                break;
        }
        new MultiMonitorTask(0, 120, deviceInfoConfig, Thread.currentThread().getName() + "-monitor", deviceWebSocketClient).start();
        List<IAvroMocker> avroMockerList = deviceInfoConfig.getAllAvroDataMockers();
        if (!CollectionUtils.isEmpty(avroMockerList)) {
            avroMockerList.forEach(avroMocker -> new AvroDataSendTask(0, avroMocker.getTaskInterval(), deviceInfoConfig, avroMocker.getTaskName(), avroMocker).start());
        }
        List<IFileMocker> fileMockerList = deviceInfoConfig.getAllFileDataMockers();
        if (!CollectionUtils.isEmpty(fileMockerList)) {
            fileMockerList.forEach(fileMocker -> new FileDataSendTask(0, fileMocker.getTaskInterval(), deviceInfoConfig, fileMocker.getTaskName(), fileMocker).start());
        }
        // 打开configuration开关
        deviceWebSocketClient.send("<msg category=\"cloud_platform_configuration\" type=\"cloud_platform_configuration_switch\" result=\"on\"></msg>");
        // Config @rtzhang
    }
}
