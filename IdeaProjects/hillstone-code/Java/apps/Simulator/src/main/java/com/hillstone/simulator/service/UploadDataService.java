package com.hillstone.simulator.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.mocker.IFileMocker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: bohuachen
 * @date: 2023/6/19 17:12
 * @description: 上送schema
 */
@Slf4j
@Service
public class UploadDataService {



    @Autowired
    private RestTemplate restTemplate;

    /**
     * 上送schema
     *
     * @param version
     * @param deviceInfo 多线程情况下区分不同sn拼接url
     */
    public void uploadSchemas(String version,DeviceInfoConfig deviceInfo) {
        if (deviceInfo.getMd5Mockers().containsKey(version)) {
            IAvroMocker avroMocker = deviceInfo.getMd5Mockers().get(version);


            String url = deviceInfo.getHttpUrl() + "/1.0/uploadSchema/file/" + avroMocker.getCategory()
                    + "/" + avroMocker.getType() + "/" + deviceInfo.getSn() + "/" + avroMocker.getAvroMd5();
            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("multipart/form-data");
            headers.setContentType(type);

            //设置请求体，注意是LinkedMultiValueMap
            InputStream is = this.getClass().getResourceAsStream(avroMocker.getAvroFilePath());
            File cacheFile = FileUtil.writeFromStream(is, UUID.randomUUID().toString());
            try {
                FileSystemResource fileSystemResource = new FileSystemResource(cacheFile);
                MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
                form.add("file", fileSystemResource);
                form.add("filename", avroMocker.getAvroFileName());
                HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
                String s = restTemplate.postForObject(url, files, String.class);
                log.info(url + " " + s);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                FileUtil.del(cacheFile);
            }
        }
    }

    /**
     * 上送avro
     *
     * @param version
     * @throws Exception
     */
    public void uploadAvro(String version,DeviceInfoConfig deviceInfo) throws Exception {
        if (deviceInfo.getMd5Mockers().containsKey(version)) {
            IAvroMocker avroMocker = deviceInfo.getMd5Mockers().get(version);
            String url = deviceInfo.getHttpUrl() + deviceInfo.getDataUploadVersion() + "/data/avro/" + avroMocker.getCategory()
                    + "/" + avroMocker.getType() + "/" + deviceInfo.getSn() + "/" + avroMocker.getAvroMd5();
            try {
                //设置请求体，注意是LinkedMultiValueMap
                MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
                form.add("data", new ByteArrayResource(avroMocker.createDataFile(deviceInfo)) {
                    @Override
                    public String getFilename() {
                        return UUID.randomUUID().toString();
                    }
                });
                //设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                String s = restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
                log.info(url + " " + s);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 上送file
     *
     * @param fileMocker
     */
    public void uploadFile(IFileMocker fileMocker,DeviceInfoConfig deviceInfo) {
        String url = deviceInfo.getHttpUrl() + deviceInfo.getDataUploadVersion() + "/data/file/" + fileMocker.getCategory()
                + "/" + fileMocker.getType() + "/" + deviceInfo.getSn() + "/" + fileMocker.getFileMd5();

        try {
            //设置请求体，注意是LinkedMultiValueMap
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("data", new ByteArrayResource(fileMocker.createFileData(deviceInfo)) {
                @Override
                public String getFilename() {
                    return UUID.randomUUID().toString();
                }
            });
            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            String s = restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
            log.info(url + " " + s);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
