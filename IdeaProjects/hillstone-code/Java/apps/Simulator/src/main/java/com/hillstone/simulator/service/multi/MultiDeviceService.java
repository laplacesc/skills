package com.hillstone.simulator.service.multi;

import com.hillstone.simulator.client.MultiDeviceWebSocketClient;
import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.service.UploadDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 多线程运行
 *
 * @author rtzhang
 * @date 2023/10/30 18:21
 * @description
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "multi.enable", havingValue = "true")
public class MultiDeviceService {
    @Autowired
    private UploadDataService uploadDataService;
    @Autowired
    private SnWebsocket snWebsocket;
    @Autowired
    private DeviceInfoConfig deviceInfoConfig;
    public static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    @Value("${multi.max-multi}")
    private int maxMulti;
    @Value("${multi.sleep-time}")
    private int sleepTime;

    /**
     * 启动多线程
     * 1、初始化指定数量的websocket
     * 2、配置参数sn
     * 3、connect
     * 4、如果断开重连
     * <p>
     * <p>
     * 可能出现如下问题
     * 1、如果同时启动所有线程，appserver大概率扛不住，部分socket会没有pong，
     * 2、如果分批连接还是会出现1000异常，只能单独while(true) 进行重连
     *
     * @throws InterruptedException
     */
    public void startAll() throws InterruptedException {
        snWebsocket.init();
        log.info("设备数量：{}", SnWebsocket.getSnMap().size());
        //并发量高的时候不能一起启动
        int temp = 0;
        for (Map.Entry<String, MultiDeviceConfigModel> entry : SnWebsocket.getSnMap().entrySet()) {
            temp += 1;
            if (sleepTime > 0 && temp % maxMulti == 0) {
                Thread.sleep(sleepTime);
            }
            new MultiDeviceRegister(entry.getValue(), deviceInfoConfig, uploadDataService).deviceRegister();

        }
        //这个每30s打印一次链接数量
        pool.scheduleAtFixedRate(() -> log.warn("连接数量: {}", MultiDeviceWebSocketClient.atomicInteger.get()), 0, 30, TimeUnit.SECONDS);
        //重连机制，没有可以重连的设备时sleep30s
        while (true) {
            String sn = SnWebsocket.getCloseSn().poll();
            if (sn == null) {
                try {
                    Thread.sleep(30000);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                log.info("设备重连: {}", sn);
                reconnect(sn);
            }
        }
    }

    /**
     * 重连
     * 机制是维护一个sn队列，触发{MultiDeviceWebSocketClient#onClose}时，将sn写入队列，这里无限循环进行重连
     * WebSocketClient 这玩意重连必须使用新的对象。。。
     *
     * @param sn
     */
    public void reconnect(String sn) {
        MultiDeviceConfigModel multiDeviceConfigModel = SnWebsocket.getSnMap().get(sn);
        if (multiDeviceConfigModel.getMultiDeviceWebSocketClient().isClosed()) {
            try {
                MultiDeviceWebSocketClient reConnectClient = snWebsocket.createWebSocketClient();
                multiDeviceConfigModel.setMultiDeviceWebSocketClient(reConnectClient);
                new MultiDeviceRegister(multiDeviceConfigModel, deviceInfoConfig, uploadDataService).deviceRegister();

            } catch (Exception e) {
                log.error("重连失败");
                log.error(e.getMessage(), e);
            }
        }
    }

}
