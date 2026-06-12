package sc.laplace.test.hillstone.qihoo.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jxwu
 */
public class QihooHw {

    public static void main(String[] args) throws JsonProcessingException {
        String url = "https://update.ti.360.cn/api/v2/list";
        String key = "1f6f21a373e9deaeeede4d7c67ad4652";
        String salt = "05bbf350b694d895ec8770929d292d04";
        String lid = "d8cda6a2e249a626";
        String packageType = "IP_REPUTATION_V3";

        long millis = System.currentTimeMillis();
        String time = String.valueOf(millis).substring(0, 10);
        String str = packageType + time + salt;
        String sign = DigestUtils.md5Hex(str);

        Map<String, Object> header = new HashMap<>(2);
        header.put("Content-Type", "application/json");
        header.put("timestamp", time);
        header.put("key", key);
        header.put("lid", lid);
        header.put("sign", sign);

        Map<String, Object> body = new HashMap<>(2);
        body.put("type", packageType);
        body.put("data", new ArrayList<>());

        System.out.println("POST " + url);
        header.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println();
        System.out.println(new JsonMapper().writeValueAsString(body));
    }

}
