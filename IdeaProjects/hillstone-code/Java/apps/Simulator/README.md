# 模拟器使用说明

## 支持内容
1. 支持多版本设备注册流程，注册内容可调节
2. 支持多版本心跳数据
3. 支持数据上送，包含avro数据与file数据，详见[模拟器基线版本上送数据支持情况](https://doc.weixin.qq.com/sheet/e3_AQgAdwa2AH06v6ZNZj1STKCTm0gPK?scode=AD4AGAfCAAwNXXv079AQgAdwa2AH0&tab=BB08J2)
4. 支持QA性能测试（需启动指定设备sn）
5. 支持HTTP与HTTPS两种方式（默认HTTPS）
6. 支持云堤设备注册
7. 支持配置交互
8. 支持多设备并发

## 操作手册
### 支持多版本设备注册流程
即注册流程中的deviceBasicInfo可进行替换，当`device.connect.info.protocol_version=1.0`时，deviceBasicInfo不可以指定；为其他值时，可以指定；<br>
需要将指定的deviceBasicInfo数据放到`basicxml/deviceBasicInfo`的文件夹下,当前已存在两个版本，v1和default，正常情况下无需添加版本；<br>
添加版本需要将一些值设置为指定值:
```
sn = "1103508100001929";
bootFileContent = "SG6000-M-5.5R2P3.bin";
platformContent = "SG-6000-E1600";
deviceName = "DEVICE_NAME";
```
如需修改版本，主要包含三个步骤：
1. 创建版本文件夹，并放到`basicxml/deviceBasicInfo`的文件夹下
2. 文件夹中数据的一些字段需要改为指定值，否则将会无法替换
3. `application-connect.yaml`中修改`device.connect.info.basic_info_version`的值为指定版本

### 支持各版本心跳
目前云端心跳存在两个版本，单节点设备心跳和分布式设备心跳，当前模拟器使用的版本为单节点设备心跳；<br>
如需修改心跳版本，主要包含三个步骤：
1. 创建版本文件夹，并放到`basicxml/deviceBasicMonitorRealTimeData`的文件夹下
2. 文件的命名需要为 `1.xml,2.xml`，依此类推
3. `application-connect.yaml`中修改`device.connect.info.real_time_monitor_version`的值为指定版本

### 支持数据上送
#### avro数据上送
如需上送avro数据，主要包含三个步骤：
1. 创建avsc文件到指定位置，路径为`avro/{category}/{type}/{filename}`，如果需要通过avsc文件生成类文件，路径为`avsc/{category}/{type}/{filename}`，并重写 `getAvroFilePath`方法
2. 实现抽象类 `com.hillstone.simulator.mocker.IAvroMocker`，抽象类的实现需要放在`com.hillstone.simulator.mocker.avroimpl`文件夹中，否则不生效；
3. 重写上送时间间隔来控制上送频率
4. `com.hillstone.simulator.config.DeviceInfoConfig.setAllAvroDataMockers()` 方法中添加需要启动的mocker

#### file数据上送
如需上送file数据，主要包含三个步骤：
1. 实现抽象类 `com.hillstone.simulator.mocker.IFileMocker`，抽象类的实现需要放在`com.hillstone.simulator.mocker.fileimpl`文件夹中，否则不生效；
2. 重写上送时间间隔来控制上送频率
3. `com.hillstone.simulator.config.DeviceInfoConfig.setAllFileDataMockers()` 方法中添加需要启动的mocker

### 支持QA性能测试
QA性能测试需要使用`java -jar` 命令启动多个模拟器实现多设备场景，使用本版本模拟器只需要在启动命令上添加 `--device.connect.info.sn={指定sn}`即可模拟不同sn的设备；

### 支持HTTP与HTTPS两种方式
`application-connect.yaml`文件中 `device.connect.info.http_link_mode`,`device.connect.info.ws_link_mode`,`device.connect.info.server_ip`,`device.connect.info.server_port`用于配置协议与域名。

### 支持云堤设备注册
`application-connect.yaml`文件中 `device.connect.info.is_yd_device` 值设置为true即为云堤注册模式<br>
云堤环境不可以使用域名注册，只能使用ip注册

### 支持配置交互
如需添加配置交互，主要包含三个步骤：
1. 实现接口类 `com.hillstone.simulator.service.config.ConfigProcessInterface`，接口类的实现需要放在`com.hillstone.simulator.service.config.configimpl`文件夹中,并定义beanName；
2. 添加数据类型与配置处理的实现的对应关系 `com.hillstone.simulator.constant.ConfigProcessBeanEnum`，processType的值为`category+type`,processBeanName值为步骤1定义的beanName,如果只需要打印信息，无需处理，可以使用默认实现`defaultConfigProcessService`；
   接口类如下：
```java
/**
 * @author: bohuachen
 * @date: 2023/6/25 9:38
 * @description: 配置流程interface 主要分为3类 1直接打印结果，不需要处理 2需要通过控制通道返回数据， 3需要通过数据通道返回数据
 */
public interface ConfigProcessInterface {

    /**
     * 配置处理流程
     * @param mo
     */
    void runConfigProcess(MessageObject mo) throws Exception;

}
```

### 配置文件修改
配置文件仅`application-connect.yaml`可修改，其余部分 _禁止_ 改动

### 多设备并发
配置文件中application-common.yml配置
```yaml
multi:
  enable: true # 是否开启多设备，false的情况以下配置不生效
  num: 100 # 并发设备数量
  sn-file-path : cpc-simulator/src/main/resources/sn.txt # 适配测试环境部署，指明sn文件位置
  max-multi: 500 # websocket线程一批启动数量
  sleep-time: 300 # websocket每批次启动间隔
```
默认多线程关闭

配置开启后需要提供一个sn.txt,sn间以,分隔

提供了FileUtils.main() 自动生成sn的方法

multi.max-multi和multi.sleep-time两个参数时为了防止压力太大，每启动一定数量线程暂停一下

### 支持show命令
目录/resource/inspection_check_file目录下的文件，文件名是show命令，文件内容是返回结果

由于目录多数是巡检用到的show命令故目录用了inspection，如需要新增命令，需要添加文件，特殊字符及特殊情况处理见com.hillstone.simulator.service.config.configimpl.ShowConfigProcessService