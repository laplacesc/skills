package sc.laplace.test.tencent.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;

import java.util.*;

/**
 * @author jxwu
 */
@UtilityClass
public class TxSdkUtil {

    public static final TxSdkLibrary INSTANCE = Native.loadLibrary("/sdk_pro/lib/libtxsdk.so", TxSdkLibrary.class);
    public static final Map<String, String> PACKAGE_TYPE_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("META-IOC", "3");
        put("META-INGRESS", "4");
        put("META-DOMAINWHITE", "5");
        put("META-IPWHITE", "6");
        put("META-EMAIL", "8");
        put("INFO-IOC", "9");
        put("INFO-INGRESS", "10");
        put("INFO-IPLOCATION", "15");
        put("INFO-HASH", "27");
        put("INFO-VULN", "28");
    }});
    public static final Map<String, String> QUERY_TYPE_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("出站 IP 检测", "1");
        put("域名检测", "2");
        put("入站 IP 检测", "3");
        put("ip 白名单", "4");
        put("域名白名单", "5");
        put("邮箱检测", "7");
        put("地理位置（定制专用）", "9");
        put("地理位置", "10");
        put("Hash", "16");
        put("漏洞知识库", "18");
    }});
    public static final Map<String, List<String>> IOC_TYPE_2_QUERY_TYPE = Collections.unmodifiableMap(new LinkedMultiValueMap<String, String>() {{
        put("ip", Arrays.asList("出站 IP 检测", "入站 IP 检测", "ip 白名单"));
        put("domain", Arrays.asList("域名检测", "域名白名单"));
        put("file", Collections.singletonList("Hash"));
    }});

    public interface TxSdkLibrary extends Library {
        /**
         * TxSdkInit 初始化sdk，返回sdk句柄
         * <p>
         * 一、使用场景：
         * 初始化腾讯威胁情报sdk句柄
         * 二、参数说明：
         * 入参：
         * licenseFilePath: 证书文件绝对路径
         * configPath :配置文件绝对路径
         * 出参：
         * sdk句柄
         * 返回示例：{"AppKey":"0ac2a9dec939"}
         */
        int TxSdkInit(String licenseFilePath, String configPath);

        /**
         * TxInitMetaData 初始化meta数据
         */
        int TxInitMetaData();

        /**
         * TxInitInfoData 初始化info数据
         */
        int TxInitInfoData();

        /**
         * TxSdkDestroy 释放sdk句柄
         * <p>
         * 一、使用场景：
         * 释放腾讯威胁情报sdk句柄
         * 二、参数说明：
         * 入参：
         * sdk句柄
         */
        void TxSdkDestroy();

        /**
         * TxLicenseInfo 获取证书中信息
         * <p>
         * 一、使用场景：
         * 获取获取证书信息
         * 二、参数说明：
         * 入参：
         * 证书文件绝对路径
         */
        String TxLicenseInfo(String licenseFilePath);

        /**
         * TxGetActiveCodeInfo 获取激活码信息
         * <p>
         * 一、使用场景：
         * 获取激活码信息
         * 二、参数说明：
         * 入参：
         * 无
         * 出参：
         * 1、设备指纹
         * 2、激活码有效期
         * 返回事例：
         * 成功：{"equipo_id":"xxxx","expire_time":"2023-05-01"}
         */
        String TxGetActiveCodeInfo();

        /**
         * TxSdkVersion 获取SDK相关版本号信息
         */
        String TxSdkVersion();

        /**
         * TxGetDeviceFingerprint 获取设备指纹
         * <p>
         * 获取设备指纹
         * 出参：
         *
         * @return 1、有错误发生，返回nil
         * 2、没有错误返回设备指纹串
         * 返回事例：
         * m+7zMG0okHgAzbb2CMaos+Jbkpdz8IuSzbL8MmBq++M=
         */
        String TxGetDeviceFingerprint();

        /**
         * TxDataUpGrade 升级
         */
        int TxDataUpGrade(String upgradeFiles, String pkType);

        /**
         * TxTiFree 释放内存
         */
        void TxTiFree(String s);

        /**
         * TxSdkDetector sdk检测入口
         * <p>
         * 入参:
         * sdkHandler：SDK句柄
         * iType：检测类型：1-出站IP检测，2-域名检测，3-入站IP检测，4-ip白名单，5-域名白名单，6-URL检测，7-邮箱检测
         * sKey：待检测数据，1：IP/IP:PORT
         * cMod：检测模式，1-检测阻断模式，不返回上下文信息，2-分析模式，返回上下文信息
         */
        int TxSdkDetector(String iType, String sKey, String mod, byte[] dRes, int tSize);
    }
}
