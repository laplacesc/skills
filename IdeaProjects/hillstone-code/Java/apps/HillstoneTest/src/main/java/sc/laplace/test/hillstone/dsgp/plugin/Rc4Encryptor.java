package sc.laplace.test.hillstone.dsgp.plugin;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * @author jxwu
 */
public class Rc4Encryptor {

    private Rc4Encryptor() {
    }

    /**
     * 将文件进行RC4加密，加密后的文件内容就是最终加密文件中的正文部分
     *
     * @param srcFile 待加密文件
     * @param dstFile 加密后的文件
     * @return key2
     */
    public static String encryptFile(File srcFile, File dstFile) {
        File tmpFile = new File(srcFile.getPath() + ".tmp");
        try {
            // srcFile转化位待加密的临时文件，内容为16位时间戳+scrFile+20位校验和
            long epochMilli = Instant.now().toEpochMilli();
            char[] randomTimeMillis = Rc4EncryptionUtil.bytesToChars((epochMilli + "   ").getBytes());
            FileUtils.writeStringToFile(tmpFile, String.valueOf(randomTimeMillis), StandardCharsets.UTF_8, true);
            Rc4EncryptionUtil.batchWriteBytes(srcFile, tmpFile);
            String checkSumSha1 = DigestUtils.sha1Hex(Files.newInputStream(tmpFile.toPath())); // NOSONAR
            String checkSum = Rc4EncryptionUtil.transHexString(checkSumSha1);
            FileUtils.writeStringToFile(tmpFile, checkSum, StandardCharsets.ISO_8859_1, true);
            // 生成两个16位随机数key1和key2，key1+key2就是最终RC4加密的密钥
            String key1 = randomNumeric(16);
            char[] key1Chars = Rc4EncryptionUtil.bytesToChars(key1.getBytes());
            String key2 = randomNumeric(16);
            String encKey = String.valueOf(key1Chars) + key2;
            // 对临时文件进行加密
            FileUtils.writeByteArrayToFile(dstFile, new byte[0], false);
            FileUtils.writeStringToFile(dstFile, String.valueOf(key1Chars), StandardCharsets.UTF_8, false);
            Rc4EncryptionUtil.batchDecrypt(tmpFile, dstFile, encKey, Mode.ENCRYPTION);
            return key2;
        } catch (IOException e) {
            throw new RuntimeException("Encryption failure", e);
        } finally {
            FileUtils.deleteQuietly(tmpFile);
        }
    }

    public static String randomNumeric(int count) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成xml文件头
     *
     * @param head                  xml文件头信息
     * @param psk                   事先约定过的psk
     * @param salt                  事先约定过的salt
     * @param key2                  RC4密钥的后16位
     * @param encryptedFile         加密文件正文
     * @param encryptedFileWithHead 最终的加密文件
     * @return 生成xml
     */
    public static String writeXmlHead(EncryptionHead head, String psk, String salt, String key2, File encryptedFile, File encryptedFileWithHead) {
        // 16位随机数转化为Base64编码作为iv
        String ivRandom = randomNumeric(16);
        String iv = new String(Base64.getEncoder().encode(ivRandom.getBytes()));
        String magicKeySec = Rc4EncryptionUtil.transHexString(DigestUtils.sha1Hex(psk + head.getSn() + salt + head.getVersion()).substring(0, 32)); // NOSONAR
        String magic = Rc4EncryptionUtil.rc4(key2, ivRandom + magicKeySec);
        // 构建xml
        Element xml = DocumentHelper.createElement("xml");
        try {
            for (Field field : FieldUtils.getAllFieldsList(EncryptionHead.class)) {
                // jacoco bug
                if (field.isSynthetic()) {
                    continue;
                }
                String value = String.valueOf(FieldUtils.readField(head, field.getName(), true));
                xml.addElement(field.getName()).setText(value);
            }
            xml.addElement("iv").setText(iv);
            // 此处计算Base64需要enhance，不知道为什么，但是和iv不同，不知道为什么这么设计
            xml.addElement("magic").setText(Rc4EncryptionUtil.enhancedBase64(magic));
            FileUtils.writeByteArrayToFile(encryptedFileWithHead, new byte[0], false);
            FileUtils.writeStringToFile(encryptedFileWithHead, xml.asXML(), StandardCharsets.UTF_8, false);
            Rc4EncryptionUtil.batchWriteBytes(encryptedFile, encryptedFileWithHead);
            return xml.asXML();
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException("Write xml failure", e);
        }
    }

    /**
     * 直接将特征库文件加密为最终的文件，并且不再返回key2以及xml
     *
     * @param head    xml文件头信息
     * @param psk     事先约定过的psk
     * @param salt    事先约定过的salt
     * @param srcFile 待加密的原始文件
     * @param dstFile 加密后的最终文件，包括文件头的xml
     */
    public static void encryptFileWithXmlHead(EncryptionHead head, String psk, String salt, File srcFile, File dstFile) {
        File encFile = new File(dstFile.getPath() + ".enc");
        try {
            String key2 = encryptFile(srcFile, encFile);
            writeXmlHead(head, psk, salt, key2, encFile, dstFile);
        } finally {
            FileUtils.deleteQuietly(encFile);
        }
    }
}
