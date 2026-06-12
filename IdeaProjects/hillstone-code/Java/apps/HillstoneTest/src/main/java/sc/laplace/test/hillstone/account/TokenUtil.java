package sc.laplace.test.hillstone.account;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

/**
 * @author jxwu
 */
@UtilityClass
public class TokenUtil {

    /**
     * @return 生成用户对应的唯一Token
     */
    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generateToken64() {
        String token = generateToken();
        return DigestUtils.sha256Hex(token);
    }
}
