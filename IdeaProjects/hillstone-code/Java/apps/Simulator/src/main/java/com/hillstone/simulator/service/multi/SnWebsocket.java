package com.hillstone.simulator.service.multi;

import com.hillstone.simulator.client.MultiDeviceWebSocketClient;
import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.ConfigConstant;
import com.hillstone.simulator.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author rtzhang
 * @date 2023/10/31 11:39
 * @description
 */
@Slf4j
@Component
public class SnWebsocket {
    private static final Map<String, MultiDeviceConfigModel> snMap = new ConcurrentHashMap<>();
    private static final Queue<String> CLOSE_SN = new ConcurrentLinkedQueue<>();

    @Value("${multi.num}")
    private int num;
    @Value("${multi.sn-file-path}")
    private String path;
    @Autowired
    private DeviceInfoConfig deviceInfoConfig;

    @Value("${device.connect.info.ws_link_mode}")
    private String linkMode;

    /**
     * 根据设备数量初始化
     */
    public void init() {
        try {

            List<String> snNameList = FileUtils.readLines(new File(path), StandardCharsets.UTF_8);

            log.info("sn数量：{}", snNameList.size());
            log.info("启动设备数量，{}", num);
            List<String> realSnNameList = snNameList.stream().limit(num).collect(Collectors.toList());
            for (String snName : realSnNameList) {
                String[] snNameArray = snName.split(",");
                String sn = snNameArray[0];
                String name = snNameArray[1];
                snMap.put(sn, new MultiDeviceConfigModel(sn, name, createWebSocketClient()));
            }
            log.info("所有websocket初始化成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("初始化失败了");
        }

    }

    /**
     * 每次调用创建一个新的websocket链接
     *
     * @return
     * @throws URISyntaxException
     */
    public MultiDeviceWebSocketClient createWebSocketClient() throws URISyntaxException {
        MultiDeviceWebSocketClient webSocketClient = new MultiDeviceWebSocketClient(new URI(deviceInfoConfig.getWsUrl()));
        if (linkMode.equals(ConfigConstant.LINK_MODE_WSS)) {
            SSLContext sslContext = HttpUtils.getSSLContent();
            SSLSocketFactory factory = sslContext.getSocketFactory();
            webSocketClient.setSocketFactory(factory);
        }
        log.debug("websocket：{} 初始化成功", webSocketClient);
        return webSocketClient;
    }


    public static Map<String, MultiDeviceConfigModel> getSnMap() {
        return snMap;
    }

    public static Queue<String> getCloseSn() {
        return CLOSE_SN;
    }
}
