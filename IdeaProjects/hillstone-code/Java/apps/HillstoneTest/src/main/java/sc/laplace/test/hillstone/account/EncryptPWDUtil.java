package sc.laplace.test.hillstone.account;

import lombok.experimental.UtilityClass;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;

/**
 * @author jxwu
 */
@UtilityClass
public class EncryptPWDUtil {

    public static String encrypt(String s) {
        return DigestUtils.md5DigestAsHex(s.getBytes(Charset.defaultCharset())).toUpperCase();
    }
}
