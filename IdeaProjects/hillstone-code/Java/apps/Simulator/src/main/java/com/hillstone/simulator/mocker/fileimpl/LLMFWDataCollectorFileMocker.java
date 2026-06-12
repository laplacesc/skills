package com.hillstone.simulator.mocker.fileimpl;

import cn.hutool.core.lang.generator.UUIDGenerator;
import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.mocker.IFileMocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 模拟FW采集到LLM服务所需防火墙数据后的上送动作
 * 数据使用普通文本，无需avro解析
 *
 * @author cylv
 * @date 2024/7/8 13:32
 */
public class LLMFWDataCollectorFileMocker extends IFileMocker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LLMFWDataCollectorFileMocker.class);

    public static final UUIDGenerator taskIdGenerator = new UUIDGenerator();

    @Override
    public byte[] createFileData(DeviceInfoConfig device) throws IOException {

        String s = "test-upload-data";

        return s.getBytes();
    }

    @Override
    public String getCategory() {
        return "cloud_llm";
    }

    @Override
    public String getType() {
        return "rest_api";
    }

    /**
     * 此处其实携带的应为task_id，适配uploadService处理，此处用FileMd5替代
     *
     * @author cylv
     * @date 2024/7/8 13:35
     * @return java.lang.String
     */
    @Override
    public String getFileMd5() {
        return taskIdGenerator.next();
    }

    @Override
    public String getTaskName() {
        return "/cloud_llm/rest_api";
    }
}