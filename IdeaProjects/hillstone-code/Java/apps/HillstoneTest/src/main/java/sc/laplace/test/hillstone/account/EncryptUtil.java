package sc.laplace.test.hillstone.account;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author jxwu
 */
@UtilityClass
public class EncryptUtil {

    public static final Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
    private static final String KEY = "NISHIWODEXIAOYAXIAOPINGGUO,ZENME";
    private static final String CIPHER_STRING = "AES/CBC/NoPadding";
    private static final int HEXA_DECIMAL = 16;
    private static Cipher encryptCipher;
    private static Cipher decryptCipher;

    static {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");

            byte[] ivs = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            IvParameterSpec iv = new IvParameterSpec(ivs);
            encryptCipher = Cipher.getInstance(CIPHER_STRING);
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            decryptCipher = Cipher.getInstance(CIPHER_STRING);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

        } catch (Exception e) {
            logger.error("加解密类初始化失败");
            logger.error(e.getMessage(), e);

        }

    }


    /**
     * 加密
     *
     * @param s
     * @return
     */
    public static String encrypt(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        } else {
            return realEncrypt(s);
        }
    }

    /**
     * 解密
     *
     * @param s
     * @return
     */
    public static String decrypt(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        } else {
            return realDecrypt(s);
        }
    }

    /**
     * 加密
     *
     * @param sSrc
     * @return
     */
    private static String realEncrypt(String sSrc) {
        try {
            byte[] bSrc = null;
            if (sSrc.length() % HEXA_DECIMAL != 0) {
                int len = ((sSrc.length() + HEXA_DECIMAL) / HEXA_DECIMAL) * HEXA_DECIMAL;
                bSrc = new byte[len];
                Arrays.fill(bSrc, (byte) 0);
                for (int i = 0; i < sSrc.length(); i++) {
                    bSrc[i] = (byte) sSrc.charAt(i);
                }
            } else {
                bSrc = sSrc.getBytes();
            }

            byte[] encrypted = encryptCipher.doFinal(bSrc);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解密
     *
     * @param sSrc
     * @return
     */
    private static String realDecrypt(String sSrc) {
        try {

            byte[] bytes = Base64.getDecoder().decode(sSrc);

            byte[] original = decryptCipher.doFinal(bytes);
            return new String(original).trim();

        } catch (Exception ex) {
            logger.error(ex.toString());
            return null;
        }
    }


}
