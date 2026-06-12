package sc.laplace.test.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Pattern;

/**
 * @author jxwu
 */
@UtilityClass
public class DigestHelper {
    private static final Pattern SHA256_PATTERN = Pattern.compile("^[a-fA-F0-9]{64}$");

    public static String sha256Hex(String data) {
        if (data != null && SHA256_PATTERN.matcher(data).matches()) {
            return data;
        }
        return DigestUtils.sha256Hex(data);
    }
}
