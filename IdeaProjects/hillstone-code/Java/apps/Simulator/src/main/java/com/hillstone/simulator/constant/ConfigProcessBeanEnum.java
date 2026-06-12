package com.hillstone.simulator.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: bohuachen
 * @date: 2023/6/26 9:59
 * @description: 配置流程枚举
 */
public enum ConfigProcessBeanEnum {

    /**
     * cloud_platform_configuration-reboot 使用默认流程
     */
    REBOOT_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.CLOUD_REBOOT_TYPE, ConfigConstant.DEFAULT_PROCESS),
    /**
     * Inspection-show
     */
    SHOW_PROCESS(ConfigConstant.CLOUD_INSPECTION_CATEGORY + "-" + ConfigConstant.CLOUD_SHOW_TYPE, "showConfigProcessService"),
    /**
     * Config_interval 走默认流程
     */
    CONFIG_INTERVAL_LICENSE_PROCESS(ConfigConstant.CLOUD_CONFIG_CATEGORY + "-" + ConfigConstant.CLOUD_INTERVAL_TYPE, ConfigConstant.DEFAULT_PROCESS),
    /**
     * Register_ReqLogicID
     */
    REGISTER_LOGIC_ID_PROCESS(ConfigConstant.CLOUD_REGISTER_CATEGORY + "-" + ConfigConstant.CLOUD_LOGIC_ID_TYPE, "reqLogicIdProcessService"),
    /**
     * LogicID_boundinfo 走默认流程
     */
    LOGIC_ID_BOUND_INFO_PROCESS(ConfigConstant.CLOUD_LOGIC_ID_CATEGORY + "-" + ConfigConstant.CLOUD_BOUND_INFO_TYPE, ConfigConstant.DEFAULT_PROCESS),
    /**
     * tif_license 走默认流程
     */
    TIF_LICENSE_PROCESS(ConfigConstant.TIF_CATEGORY + "-" + ConfigConstant.TIF_LICENSE_TYPE, ConfigConstant.DEFAULT_PROCESS),
    ADC_PROCESS(ConfigConstant.ADC_UPDATE_CATEGORY + "-" + ConfigConstant.ADC_UPDATE_TYPE, ConfigConstant.NET_BLOCK_NAME),
    IPS_PROCESS(ConfigConstant.IPS_UPDATE_CATEGORY + "-" + ConfigConstant.IPS_UPDATE_TYPE, ConfigConstant.NET_BLOCK_NAME),
    WAF_PROCESS(ConfigConstant.WAF_UPDATE_CATEGORY + "-" + ConfigConstant.WAF_UPDATE_TYPE, ConfigConstant.NET_BLOCK_NAME),
    IPS_SIGNATURE_UPGRADE_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.CLOUD_SIGNATURE_UPGRADE_TYPE, ConfigConstant.IPS_SIGNATURE_UPGRADE_NAME),
    IPS_SIGNATURE_QUERY_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.CLOUD_SIGNATURE_QUERY_TYPE, ConfigConstant.IPS_SIGNATURE_UPGRADE_NAME),
    IP_BLOCK_ADD_CONFIG_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.IP_ADD_BLOCK_TYPE, ConfigConstant.BLOCK_CONFIG_PROCESS_NAME),
    IP_BLOCK_DEL_CONFIG_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.IP_DEL_BLOCK_TYPE, ConfigConstant.BLOCK_CONFIG_PROCESS_NAME),
    URL_BLOCK_ADD_CONFIG_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.URL_ADD_BLOCK_TYPE, ConfigConstant.BLOCK_CONFIG_PROCESS_NAME),
    URL_BLOCK_DEL_CONFIG_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.URL_DEL_BLOCK_TYPE, ConfigConstant.BLOCK_CONFIG_PROCESS_NAME),
    DOMAIN_BLOCK_ADD_CONFIG_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.DOMAIN_ADD_BLOCK_TYPE, ConfigConstant.BLOCK_CONFIG_PROCESS_NAME),
    DOMAIN_BLOCK_DEL_CONFIG_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.DOMAIN_DEL_BLOCK_TYPE, ConfigConstant.BLOCK_CONFIG_PROCESS_NAME),
    APP_LIBRARY_UPLOAD_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.SYNC_APP_LIBRARY, ConfigConstant.APP_LIBRARY_UPLOAD_CONFIG_PROCESS_SERVICE),
    DEVICE_FULL_CONFIG_UPLOAD_PROCESS(ConfigConstant.CLOUD_PLATFORM_CATEGORY + "-" + ConfigConstant.SYNC_CONFIG_FILE, ConfigConstant.DEVICE_FULL_CONFIG_PROCESS_SERVICE),

    /**
     * cloud_llm-rest_api
     */
    LLM_FW_REQUEST_PROCESS("cloud_llm-rest_api", ConfigConstant.LLM_DATA_COLLECT_PROCESS_SERVICE),
    /**
     * cloud_llm-cli_show
     */
    LLM_FW_SHOW_PROCESS("cloud_llm-cli_show", ConfigConstant.LLM_DATA_SHOW_PROCESS_SERVICE),
    /**
     * llm-rest_api
     */
    LLM_FW_API_PROCESS("llm-rest_api", ConfigConstant.LLM_DATA_API_PROCESS_SERVICE),
    ;


    /**
     * 流程type
     */
    private String processType;

    /**
     * 流程bean name
     */
    private String processBeanName;

    ConfigProcessBeanEnum(String processType, String processBeanName) {
        this.processType = processType;
        this.processBeanName = processBeanName;
    }

    public String getProcessBeanName() {
        return processBeanName;
    }

    public String getProcessType() {
        return processType;
    }

    private static final Map<String, ConfigProcessBeanEnum> configProcessMap = new HashMap<>(8);

    static {
        for (ConfigProcessBeanEnum configProcessBeanEnum : ConfigProcessBeanEnum.values()) {
            configProcessMap.put(configProcessBeanEnum.getProcessType(), configProcessBeanEnum);
        }
    }

    public static String getConfigProcess(String type) {
        return configProcessMap.get(type) != null ? configProcessMap.get(type).getProcessBeanName() : null;
    }
}
