package com.hillstone.simulator.constant;

/**
 * @author: bohuachen
 * @date: 2023/6/25 10:32
 * @description: some desc
 */
public class ConfigConstant {


    private ConfigConstant() {
    }

    public static final String DEFAULT_PROCESS = "defaultConfigProcessService";

    public static final String CLOUD_PLATFORM_CATEGORY = "cloud_platform_configuration";

    public static final String PUSH_GET_CONFIG_CATEGORY = "Config.PushGetConfig";


    /**
     * ip 阻断
     */
    public static final String CLOUD_BLOCK_IP_ADD_TYPE = "block:ip:add";
    /**
     * URL/domain阻断
     */
    public static final String CLOUD_BLOCK_URL_ADD_TYPE = "block:url:add";
    /**
     * 反向代理
     */
    public static final String CLOUD_REVERSE_SSH_TYPE = "reverse_ssh";
    /**
     * image升级
     */
    public static final String CLOUD_IMAGE_UPGRADE_CLOUDVIEW_TYPE = "image_upgrade_cloudview";
    /**
     * 设备重启
     */
    public static final String CLOUD_REBOOT_TYPE = "reboot";
    /**
     * config备份
     */
    public static final String CLOUD_CONFIG_FILE_UPLOAD_TYPE = "config_file_upload";
    /**
     * config恢复
     */
    public static final String CLOUD_CONFIG_FILE_DOWNLOAD_TYPE = "config_file_download";


    /**
     * 巡检category
     */
    public static final String CLOUD_INSPECTION_CATEGORY = "Inspection";

    /**
     * 巡检
     */
    public static final String CLOUD_SHOW_TYPE = "show";


    /**
     * 接口流量config
     */
    public static final String CLOUD_CONFIG_CATEGORY = "Config";

    /**
     * 上报周期控制
     */
    public static final String CLOUD_INTERVAL_TYPE = "interval";

    /**
     * 注册流程category
     */
    public static final String CLOUD_REGISTER_CATEGORY = "Register";

    /**
     * 逻辑id
     */
    public static final String CLOUD_LOGIC_ID_TYPE = "ReqLogicID";

    /**
     * 逻辑id category
     */
    public static final String CLOUD_LOGIC_ID_CATEGORY = "LogicID";
    /**
     * 绑定信息
     */
    public static final String CLOUD_BOUND_INFO_TYPE = "boundinfo";

    /**
     * 设备特征库升级
     */
    public static final String CLOUD_SIGNATURE_UPGRADE_TYPE = "Config.PushExecConfig";

    /**
     * 设备特征库查询
     */
    public static final String CLOUD_SIGNATURE_QUERY_TYPE = "Config.PushGetConfig";

    /**
     * 请求应用特征库上传
     */
    public static final String SYNC_APP_LIBRARY = "sync_app_library";

    public static final String SYNC_CONFIG_FILE = "sync_config_file";


    /**
     * tif category
     */
    public static final String TIF_CATEGORY = "tif";
    /**
     * license
     */
    public static final String TIF_LICENSE_TYPE = "license";

    /**
     * websocket连接模式 https还是http
     */
    public static final String LINK_MODE_WSS = "wss";
    public static final String LINK_MODE_WS = "ws";

    public static final String ADC_UPDATE_CATEGORY = "server_loadbalance";
    public static final String IPS_UPDATE_CATEGORY = "critical_asset";
    public static final String WAF_UPDATE_CATEGORY = "waf_site";

    public static final String ADC_UPDATE_TYPE = "update_virtual_servers";
    public static final String IPS_UPDATE_TYPE = "update_critical_asset";
    public static final String WAF_UPDATE_TYPE = "update_waf_sites";

    public static final String NET_BLOCK_NAME = "netBlockConfigProcessService";

    public static final String IPS_SIGNATURE_UPGRADE_NAME = "ipsSignatureUpgradeConfigProcessService";

    public static final String BLOCK_CONFIG_PROCESS_NAME = "blockConfigProcessService";
    public static final String IP_ADD_BLOCK_TYPE = "block:ip:add";
    public static final String IP_DEL_BLOCK_TYPE = "block:ip:del";
    public static final String URL_ADD_BLOCK_TYPE = "block:url:add";
    public static final String URL_DEL_BLOCK_TYPE = "block:url:del";
    public static final String DOMAIN_ADD_BLOCK_TYPE = "block:domain:add";
    public static final String DOMAIN_DEL_BLOCK_TYPE = "block:domain:del";
    public static final String DEVICE_FULL_CONFIG_PROCESS_SERVICE = "DeviceFullConfigProcessService";

    public static final String APP_LIBRARY_UPLOAD_CONFIG_PROCESS_SERVICE = "appLibraryUploadProcessService";

    public static final String LLM_DATA_COLLECT_PROCESS_SERVICE = "llmDataCollectProcessService";
    public static final String LLM_DATA_API_PROCESS_SERVICE = "llmDataApiProcessService";
    public static final String LLM_DATA_SHOW_PROCESS_SERVICE = "llmDataShowProcessService";


}
