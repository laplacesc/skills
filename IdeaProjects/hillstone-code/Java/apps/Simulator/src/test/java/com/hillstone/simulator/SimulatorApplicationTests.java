package com.hillstone.simulator;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.mocker.avroimpl.tif.TifTokenMocker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SimulatorApplicationTests {


    @Autowired
    DeviceInfoConfig deviceInfoConfig;

    @Test
    void contextLoads() {

        List<IAvroMocker> avroMockerList = new ArrayList<>();
        // 添加需要导出的avro
        avroMockerList.add(new TifTokenMocker());
        for (IAvroMocker avroMocker : avroMockerList) {
            try {


                String fileName = avroMocker.getCategory() + "-" + avroMocker.getType() + "-" + avroMocker.getAvroMd5() + ".avro";
                // 指定输出文件路径
                File file = new File(File.separator + "avro" + File.separator + fileName);

                // 创建FileOutputStream对象
                FileOutputStream fos = new FileOutputStream(file);

                // 将byte数组写入文件
                fos.write(avroMocker.createDataFile(deviceInfoConfig));

                // 关闭输出流
                fos.close();

                System.out.println("文件已成功创建 " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
