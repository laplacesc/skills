package sc.laplace.test.ping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import sc.laplace.test.ping.model.devicequery.Msg;
import sc.laplace.test.ping.model.vod.DeviceIdentifier;
import sc.laplace.test.ping.model.vod.VodActivationCode;
import sc.laplace.test.ping.model.vod.VodDeviceDetectionData;
import sc.laplace.test.ping.util.CipherUtil;
import sc.laplace.test.ping.util.HttpClientUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;

@Slf4j
class MainTest {
    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    @Test
    void deviceEncryptionTest() throws IOException, GeneralSecurityException, URISyntaxException {
        // 128位密钥 = 16 bytes Key
        String key = "VtmMEXRew144dZRz";
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        Msg msg = Msg.builder()
                .deviceSn("WASD382354820500")
                .devicePlatform("SG-6000-S3500")
                .requestTime(LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
        Map<String, Object> map = JSON_MAPPER.convertValue(msg, Map.class);
        map.put("test_key", "test_value");
        // 加密
        byte[] encrypted = CipherUtil.encrypt(keyBytes, JSON_MAPPER.writeValueAsBytes(map));
        // base64 编码
        String base64Encode = Base64.getEncoder().encodeToString(encrypted);
        log.info("msg base64 encode: {}", base64Encode);
        // url 编码
        String urlEncode = URLEncoder.encode(base64Encode, StandardCharsets.UTF_8.name());
        log.info("msg url encode: {}", urlEncode);

        // url 解码
        String urlDecode = URLDecoder.decode(urlEncode, StandardCharsets.UTF_8.name());
        log.info("msg url decode: {}", urlDecode);
        // base64 解码
        byte[] base64Decode = Base64.getDecoder().decode(urlDecode);
        // 解密
        String decrypted = new String(CipherUtil.decrypt(keyBytes, base64Decode), StandardCharsets.UTF_8);
        log.info("msg decrypted: {}", decrypted);

        HttpGet request = new HttpGet(new URIBuilder("https://ti.hillstonenet.com.cn/device/ioc/ip/geo?msg=" + urlEncode).build());
        Map<String, Object> response = HttpClientUtil.execute(request, new TypeReference<Map<String, Object>>() {
        });
        log.info("response: {}", JSON_MAPPER.writeValueAsString(response));
    }

    @Test
    void cipherUtilTest() throws UnsupportedEncodingException, JsonProcessingException, GeneralSecurityException {
        String key = "kWAVHza5M2rQVzNP";
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        DeviceIdentifier deviceIdentifier = DeviceIdentifier.builder()
                .deviceSn("WASD382354820500")
                .devicePlatform("SG-6000-S3500")
                .generatedTime(1758264153016L)
                .build();
        String di = Base64.getEncoder().encodeToString(CipherUtil.encrypt(keyBytes, JSON_MAPPER.writeValueAsBytes(deviceIdentifier)));
        log.info("device_identifier: {}", di);

        VodActivationCode vodActivationCode = VodActivationCode.builder()
                .deviceSn("WASD382354820500")
                .generatedTime(1758264153016L)
                .vodIp("10.182.215.104")
                .vodPort(8080)
                .vodDetectionUrl("http://10.182.215.104:8080/vod/detection")
                .vodAddress("10.182.215.104:8080")
                .build();
        String vac = Base64.getEncoder().encodeToString(CipherUtil.encrypt(keyBytes, JSON_MAPPER.writeValueAsBytes(vodActivationCode)));
        log.info("vod_activation_code: {}", vac);

        VodDeviceDetectionData vodDeviceDetectionData = VodDeviceDetectionData.builder()
                .deviceSn("WASD382354820500")
                .requestTime(1758264153016L)
                .localIp("10.182.215.104")
                .build();
        String dr = Base64.getEncoder().encodeToString(CipherUtil.encrypt(keyBytes, JSON_MAPPER.writeValueAsBytes(vodDeviceDetectionData)));
        log.info("device_request: {}", dr);
        log.info("device_request url decode: {}", URLEncoder.encode(dr, StandardCharsets.UTF_8.name()));
    }
}