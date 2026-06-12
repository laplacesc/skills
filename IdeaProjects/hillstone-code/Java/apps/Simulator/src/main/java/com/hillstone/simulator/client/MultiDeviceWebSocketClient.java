package com.hillstone.simulator.client;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.SimulatorConstant;
import com.hillstone.simulator.controller.DeviceController;
import com.hillstone.simulator.service.multi.MultiDeviceRegister;
import com.hillstone.simulator.service.multi.SnWebsocket;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多设备用的客户端 区别于单例
 *
 * @author: bohuachen
 * @date: 2023/6/16 8:30
 * @description: some desc
 */
@Slf4j
public class MultiDeviceWebSocketClient extends WebSocketClient {
    public static final AtomicInteger atomicInteger = new AtomicInteger(0);

    MultiDeviceRegister multiDeviceRegister;

    public MultiDeviceWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public void setRunnable(MultiDeviceRegister multiDeviceRegister) {
        this.multiDeviceRegister = multiDeviceRegister;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("websocket open !!!!! sn :{}", multiDeviceRegister.getDeviceInfoConfig().getSn());
        multiDeviceRegister.helloProcess();
        atomicInteger.getAndAdd(1);
    }

    @Override
    public void onMessage(String msg) {
        log.info("get websocket msg sn : {}, {}", multiDeviceRegister.getDeviceInfoConfig().getSn(), msg);
        multiDeviceRegister.messageProcess(msg);
    }

    @Override
    public void onClose(int closeCode, String reason, boolean b) {
        atomicInteger.getAndAdd(-1);

        log.info("websocket close : sn:{} closeCode : {}, reason : {}", multiDeviceRegister.getDeviceInfoConfig().getSn(), closeCode, reason);

        if (SpringUtils.getBean(DeviceInfoConfig.class).getProcessModel() == SimulatorConstant.PROCESS_MODE_AUTO) {
            //自动重连
            SnWebsocket.getCloseSn().add(multiDeviceRegister.getDeviceInfoConfig().getSn());
        } else {
            // 下线清除map数据
            SpringUtils.getBean(DeviceController.class).getWebsocketMap().remove(multiDeviceRegister.getDeviceInfoConfig().getSn());
        }


    }

    @Override
    public void onError(Exception e) {
        log.error(e.getMessage(), e);
    }
}
