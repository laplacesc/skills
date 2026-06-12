package com.hillstone.simulator.utils;

import com.hillstone.simulator.config.DeviceInfoConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: bohuachen
 * @date: 2023/6/20 9:21
 * @description: some desc
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * 该方法替换了sn platform 设备名称，不要乱用
     *
     * @param content
     * @param device
     * @return
     */
    public static String replaceDeviceInfo(String content, DeviceInfoConfig device) {
        String sn = "1103508100001929";
        // 设备名称
        String bootFileContent = "SG6000-M-5.5R2P3.bin";
        String platformContent = "SG-6000-E1600";
        String deviceName = "DEVICE_NAME";
        content = content.replace(sn, device.getSn()).replace(bootFileContent, device.getBootFile())
                .replace(platformContent, device.getPlatform())
                .replace(deviceName, device.getFwName());
        return content;

    }

    /**
     * 该方法替换了sn platform 设备名称，不要乱用
     *
     * @param content
     * @param device
     * @param deviceSn 多线程情况
     * @return
     */
    public static String replaceDeviceInfo(String content, DeviceInfoConfig device, String deviceSn) {
        String sn = "1103508100001929";
        // 设备名称
        String bootFileContent = "SG6000-M-5.5R2P3.bin";
        String platformContent = "SG-6000-E1600";
        String deviceName = "DEVICE_NAME";
        content = content.replace(sn, deviceSn).replace(bootFileContent, device.getBootFile())
                .replace(platformContent, device.getPlatform())
                .replace(deviceName, device.getFwName());
        return content;

    }

    /**
     * 会读取<timestamp></timestamp>和 sandboxExpiredTime 进行替换
     *
     * @param ins
     * @return
     * @throws IOException
     */
    public static String replaceDeviceInfo(InputStream ins) throws IOException {
        String ret = "";

        String sandboxExpiredTime = "<expired-time>Permanent.Upgrade effective time 2018/12/04</expired-time>";


        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(ins, StandardCharsets.UTF_8));
            // 最好在将字节流转换为字符流的时候 进行转码
            StringBuilder buffer = new StringBuilder();

            String line = "";

            while ((line = bf.readLine()) != null) {

                if (line.contains("timestamp")) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    line = "<timestamp>" + df.format(new Date()) + "</timestamp>";
                }
                if (line.contains(sandboxExpiredTime)) {
                    Date dNow = new Date(System.currentTimeMillis() + 2592000000L);
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd ");
                    String dataStr = ft.format(dNow);
                    line = "<expired-time>Permanent.Upgrade effective time " + dataStr + "</expired-time>";
                }
                buffer.append(line);
            }
            ret = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static void main(String[] args) throws IOException {
        //生成sn
        String snPrefix = "rtzhangtest";
        List<String> snNameList = new ArrayList<>();
        int num = 5000;
        int buwei = 16 - snPrefix.length();
        String name = "defaultaccount";
        for (int i = 0; i < num; i++) {
            StringBuilder snName = new StringBuilder();
            snName.append(snPrefix).append(String.format("%0" + buwei + "d", i)).append(",").append(name);
            snNameList.add(snName.toString());
        }
        org.apache.commons.io.FileUtils.writeLines(new File("cpc-simulator/src/main/resources/sn.txt"), snNameList);
    }

}
