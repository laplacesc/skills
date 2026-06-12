package com.hillstone.simulator.constant;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:47
 * @description: some desc
 */
public class AvroConstant {

    private AvroConstant() {
    }

    public static final String BASE_PATH = "/avro";
    public static final String BASE_AVSC_PATH = "/avsc";
    public static final String DEMO_FILE_PATH = "/demofile";

    public static final String RET_SCHEMAS = "<msg category=\"Register\" type=\"requestSchemas\" result=\"ok\"> </msg>";

    public static final String THREAT_EVENT_CATEGORY = "event";
    public static final String THREAT_EVENT_TYPE = "threat_event";
    public static final String THREAT_EVENT_AVRO_FILENAME = "16c07e790cc04e9b60c5c7cb9602f8d2.avsc";
    public static final String THREAT_EVENT_MD5 = "16c07e790cc04e9b60c5c7cb96021111";
    public static final String SPEED = "no";
    public static final String AGGREGATION = "no";
    // data lake
    public static final String AV_STATUS_CATEGORY = "av_report";
    public static final String AV_STATUS_TYPE = "av_status";
    public static final String AV_STATUS_MD5 = "9b58315cefd685b5840d98ab4f1d8529";
    public static final String AV_STATUS_AVRO_FILENAME = "9b58315cefd685b5840d98ab4f1d8529.avsc";

    public static final String AD_STATUS_CATEGORY = "ad_report";
    public static final String AD_STATUS_TYPE = "hit_counter";
    public static final String AD_STATUS_MD5 = "3e8fa65550ac42d3c322b7d9c6ea2525";
    public static final String AD_STATUS_AVRO_FILENAME = "3e8fa65550ac42d3c322b7d9c6ea2525.avsc";

    /**
     * 事件日志
     */
    public static final String LOG_EVENT_REPORT_CATEGORY = "logd_report";
    public static final String LOG_EVENT_REPORT_TYPE = "event_report";
    public static final String LOG_EVENT_REPORT_MD5 = "85b3314c5bfeaf131894adeaefe5ca8d";
    public static final String LOG_EVENT_REPORT_FILENAME = "85b3314c5bfeaf131894adeaefe5ca8d.avsc";
    /**
     * 事件日志--更新后
     */
    public static final String LOG_EVENT_REPORT_MD5_V2 = "d67b25d9f218d51c0c145c27620d05fd";
    public static final String LOG_EVENT_REPORT_FILENAME_V2 = "d67b25d9f218d51c0c145c27620d05fd.avsc";

    public static final String IOT_MONITOR_REPORT_CATEGORY = "iot_monitor_report";
    public static final String IOT_MONITOR_DEVICE_INFO_TYPE = "iot_device_info";
    public static final String IOT_MONITOR_DEVICE_INFO_MD5 = "e841ef79737bc111d7d28f54e5d262c2";
    public static final String IOT_MONITOR_DEVICE_INFO_FILENAME = "e841ef79737bc111d7d28f54e5d262c2.avsc";



    public static final String IOT_REPORT_CATEGORY = "iot_monitor_report";
    public static final String IOT_REPORT_TYPE = "iot_monitor";
    public static final String IOT_REPORT_V2_MD5 = "f283ec6b00c4683e1d8b5f28ebd64692";
    public static final String IOT_REPORT_V2_FILENAME = "f283ec6b00c4683e1d8b5f28ebd64692.avsc";
    public static final String IOT_REPORT_V1_MD5 = "d1da11fb9f3f725cbbc039aa9555ff85";
    public static final String IOT_REPORT_V1_FILENAME = "d1da11fb9f3f725cbbc039aa9555ff85.avsc";
    public static final String STATISTICS_SET_DYNAMIC_CATEGORY = "statistics_set_dynamic";
    public static final String TRAFFIC_RANK_TYPE = "traffic_rank";
    public static final String TRAFFIC_RANK_MD5 = "75b2e548b386f4a9293b4c376f8dc233";
    public static final String TRAFFIC_RANK_FILENAME = "75b2e548b386f4a9293b4c376f8dc233.avsc";

    public static final String PCAP_INFO_CATEGORY = "forensic";
    public static final String PCAP_INFO_TYPE = "pcap";
    public static final String PCAP_INFO_MD5 = "d3973ccf0836044780ae598b4dec1b82";
    public static final String PCAP_INFO_FILENAME = "d3973ccf0836044780ae598b4dec1b82.avsc";


    /**
     * ADC 一键断网avro
     */
    public static final String SERVER_LOADBALANCE_CATEGORY = "server_loadbalance";
    public static final String VIRTUAL_SERVERS_MANAGEMENT_TYPE = "virtual_servers_management";
    public static final String ADC_MANAGE_FILENAME = "95b2e092f35393f955585a69d3414fdc.avsc";
    public static final String ADC_MANAGE_MD5 = "9fc51b3bbd39506ae982279e79c5ed33";
    /**
     * 合入R10之后版本的schema
     */
    public static final String ADC_MANAGE_V2_FILENAME = "95b2e092f35393f955585a69d3414fdc.avsc";
    public static final String ADC_MANAGE_V2_MD5 = "95b2e092f35393f955585a69d3414fdc";

    public static final String VIRTUAL_SERVERS_TRAFFICS_TYPE = "virtual_servers_traffics";
    public static final String ADC_TRAFFICS_FILENAME = "47dccdb75631af2ba86ac449db1256c2.avsc";
    public static final String ADC_TRAFFICS_MD5 = "47dccdb75631af2ba86ac449db1256c2";
    /**
     * waf
     */
    public static final String WAF_SITE_CATEGORY = "waf_site";
    public static final String WAF_SITE_CONFIG_TYPE = "waf_site_config";
    public static final String WAF_SITE_FILENAME = "feedf37c2e7f7c9ab9f6c6b9e43b99e8.avsc";
    public static final String WAF_SITE_MD5 = "feedf37c2e7f7c9ab9f6c6b9e43b99e8";
    /**
     * Ips
     */
    public static final String CRITICAL_ASSET_CATEGORY = "critical_asset";
    public static final String CRITICAL_ASSET_MANAGEMENT_TYPE = "critical_asset_management";
    public static final String CRITICAL_ASSET_MANAGEMENT_FILENAME = "641da66efe11943b06e80fb551741a6f.avsc";
    public static final String CRITICAL_ASSET_MANAGEMENT_MD5 = "641da66efe11943b06e80fb551741a6f";

    public static final String CRITICAL_ASSET_TRAFFICS_TYPE = "critical_asset_traffics";
    public static final String CRITICAL_ASSET_TRAFFICS_FILENAME = "1a39d91fd53b1e716fa7e6a51ee0b332.avsc";
    public static final String CRITICAL_ASSET_TRAFFICS_MD5 = "1a39d91fd53b1e716fa7e6a51ee0b332";
}
