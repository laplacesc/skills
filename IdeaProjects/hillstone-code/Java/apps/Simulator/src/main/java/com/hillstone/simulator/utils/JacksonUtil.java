package com.hillstone.simulator.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ymfang
 * @date 2021/12/31 15:56
 */
public class JacksonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    private JacksonUtil() {

    }

    /**
     * json to object
     * @param t
     * @param jsonStr
     * @param <T>
     * @return
     */
    public static <T> T toObject(String jsonStr, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(jsonStr, t);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * json to object failed to unknown properties
     * @param jsonStr
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T toObjectStrict(String jsonStr, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonStr, t);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * json to object ignore annotation
     * @param jsonStr
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T toObjectIgnoreAnnotation(String jsonStr, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            return objectMapper.readValue(jsonStr, t);
        } catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * json to object ignore annotation
     * @param data
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T toObjectIgnoreAnnotation(byte[] data, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            return objectMapper.readValue(data, t);
        } catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * json to jsonNode
     * @param jsonStr
     * @return
     */
    public static JsonNode toTree(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return  objectMapper.readTree(jsonStr);
        } catch (IOException e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * jsonNode to Object
     * @param jsonNode
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T toObject(JsonNode jsonNode, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.treeToValue(jsonNode, t);
        } catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * byte array to object
     * @param data
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T toObject(byte[] data, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(data, t);
        } catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * object to json
     * @param o
     * @return
     */
    public static String toJson(Object o){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return  objectMapper.writeValueAsString(o);
        } catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * object to json ignore annotation
     * @param o
     * @return
     */
    public static String toJsonIgnoreAnnotation(Object o){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        try{
            return  objectMapper.writeValueAsString(o);
        } catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * json to arrayList
     * @param jsonStr
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String jsonStr, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, t);
        try {
            return objectMapper.readValue(jsonStr, collectionType);
        } catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        return new ArrayList<>();
    }

    public static <T> T toObject(String jsonStr, TypeReference<T> typeReference) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(jsonStr, typeReference);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
