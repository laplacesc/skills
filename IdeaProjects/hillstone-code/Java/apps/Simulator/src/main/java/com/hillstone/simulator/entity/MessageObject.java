package com.hillstone.simulator.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.FlyweightAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:18
 * @description: some desc
 */
@Slf4j
public class MessageObject {

    private String category;

    private String xmlString;

    private String result;
    private String type;

    private List<Element> elements;

    public MessageObject(String args) {

        this.xmlString = args;
        Document doc;
        try {
            doc = DocumentHelper.parseText(args);
            Element root = doc.getRootElement();

            this.category = root.attributeValue("category");
            this.type = root.attributeValue("type");
            this.result = root.attributeValue("result");

            elements = root.elements();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    public MessageObject(String category, String type, List<Element> elements) {
        this.category = category;
        this.type = type;
        this.elements = elements;
    }

    public MessageObject(String category, String type, Element e) {
        this.category = category;
        this.type = type;
        this.elements = new ArrayList<>();
        this.elements.add(e);
    }

    public MessageObject(String category, String type) {
        this.category = category;
        this.type = type;
        this.elements = new ArrayList<>();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getXmlString() {
        if (category == null || type == null) {
            return null;
        }

        if (StringUtils.isEmpty(xmlString)) {
            this.reBuild();
        }

        return xmlString;
    }

    public void setXmlString(String xmlString) {
        this.xmlString = xmlString;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        if (StringUtils.isNotBlank(this.xmlString)) {
            this.reBuild();
        }
    }

    @Override
    public String toString() {
        return this.asXML();
    }

    public String asXML() {
        try {
            this.getXmlString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xmlString;
    }

    private void reBuild() {
        if (category == null || type == null) {
            log.info("to handle an invalid msg");
        } else {
            Element temp = DocumentHelper.createElement("msg");
            temp.add(new FlyweightAttribute("category", category));
            temp.add(new FlyweightAttribute("type", type));
            if (StringUtils.isNotEmpty(this.result)) {
                temp.add(new FlyweightAttribute("result", result));
            }

            temp.setContent(elements);
            xmlString = temp.asXML();
        }
    }

    public boolean isValid() {
        return category != null && type != null;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;

        if (StringUtils.isNotBlank(this.xmlString)) {
            this.reBuild();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (StringUtils.isNotBlank(this.xmlString)) {
            this.reBuild();
        }
    }

}
