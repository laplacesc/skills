package com.hillstone.simulator.utils;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bohuachen
 * @date 2025/3/13 14:47
 * @description
 */
@Slf4j
public class LLMUtils {


    /**
     * 获取taskId
     *
     * @param mo
     * @return
     */
    public static String getTaskId(String mo) {
        Document document;
        try {
            document = DocumentHelper.parseText(mo);
            Element rootElement = document.getRootElement();
            return rootElement.elementText("task_id");
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 提取xml中的cmd
     *
     * @param xml
     * @return
     */
    public static List<String> extractCmdList(String xml) {
        List<String> cmdList = new ArrayList<>();
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element cmdsElement = root.element("cmds"); // 获取cmds父元素
            if (cmdsElement != null) {
                List<Element> elementList = cmdsElement.elements("cmd");
                // 遍历所有<cmd>子元素
                for (Element cmd : elementList) {
                    cmdList.add(cmd.getTextTrim());
                }
            }
        } catch (DocumentException e) {
            log.error("XML解析失败", e);
        }
        return cmdList;
    }
}
