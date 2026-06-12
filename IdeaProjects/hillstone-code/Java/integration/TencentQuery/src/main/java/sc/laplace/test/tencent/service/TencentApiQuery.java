package sc.laplace.test.tencent.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sc.laplace.test.tencent.model.TencentQueryParam;
import sc.laplace.test.tencent.util.JsonUtil;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author jxwu
 */
@Slf4j
@Service
public class TencentApiQuery {

    private static final String STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    @Resource
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public Object query(String key, String type) {
        Map<Object, Object> result = restTemplate.postForObject("https://xti.qq.com/api/v3/ti", getParam(key, type),
                Map.class);
        log.info("ioc: {}, type: {}, result: {}", key, type, JsonUtil.toJson(result));
        if (null != result && (Integer) result.get("return_code") == 0) {
            result.put("key", key);
            return result;
        }
        return null;
    }

    public TencentQueryParam getParam(String key, String type) {
        TencentQueryParam param = TencentQueryParam.builder()
                .cAppId("89dcac88d9")
                .cVersion("3.0")
                .cNonce(IntStream.generate(() -> SECURE_RANDOM.nextInt(STRING.length()))
                        .limit(5)
                        .mapToObj(STRING::charAt)
                        .map(c -> "" + c)
                        .reduce("", (a, b) -> a + b))
                .cTimestamp(System.currentTimeMillis() / 1000)
                .key(key)
                .option(1)
                .build();
        switch (type) {
            case "file":
                param.setCAction("FileInfo");
                param.setType("md5");
                break;
            case "domain":
                param.setCAction("DomainInfo");
                param.setType(type);
                break;
            case "ip":
                param.setCAction("IPAnalysis");
                param.setType(type);
                break;
            default:
        }
        String signature = "c_action=" + param.getCAction() +
                "&c_appid=" + param.getCAppId() +
                "&c_nonce=" + param.getCNonce() +
                "&c_timestamp=" + param.getCTimestamp() +
                "&c_version=" + param.getCVersion() +
                "&key=" + param.getKey() +
                "&option=" + param.getOption() +
                "&type=" + param.getType() +
                "f32f6f7646110ef86b87c4c40ca144ca";
        param.setCSignature(DigestUtils.sha256Hex(signature));
        return param;
    }
}
