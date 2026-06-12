package sc.laplace.test.hillstone.dsgp.plugin;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


/**
 * @author jxwu
 */
public class Rc4Decryptor {

    private Rc4Decryptor() {
    }

    /**
     * 用于对RC4加密后的文件进行解密
     *
     * @param srcFile 待解密的文件
     * @param dstFile 解密后的文件
     * @param psk     解密所需的psk
     * @param salt    解密所需的salt
     */
    public static void decryptFile(File srcFile, File dstFile, String psk, String salt) {
        File tmpFile = null;
        File rc4File = null;
        try {
            // 读取文件有的xml
            String headXml = Rc4EncryptionUtil.getHeadXml(srcFile);
            Element rootElement = DocumentHelper.parseText(headXml).getRootElement();
            String sn = rootElement.elementTextTrim("sn");
            String version = rootElement.elementTextTrim("version");
            String iv = rootElement.elementTextTrim("iv");
            String magic = rootElement.elementTextTrim("magic");
            // 获取encKey中的key2以及相关操作
            String magicKeySec = Rc4EncryptionUtil.transHexString(DigestUtils.sha1Hex(psk + sn + salt + version).substring(0, 32)); // NOSONAR
            char[] ivChars = Rc4EncryptionUtil.decryptByBase64(iv);
            char[] magicChars = Rc4EncryptionUtil.decryptByBase64(magic);
            // 解码后的iv+magicKeySec作为key2的密钥
            String key2 = Rc4EncryptionUtil.rc4(String.valueOf(magicChars), String.valueOf(ivChars) + magicKeySec);
            // 读取加密文件的正文的前16个子节，得到随机数
            char[] randomNumber = Rc4EncryptionUtil.readRandomNumber(srcFile, headXml.length(), 16);
            // 16位随机数 + key1 得到 encKey
            String encKey = String.valueOf(randomNumber) + key2;
            // 在加密文件中取正文存到临时文件
            tmpFile = new File(dstFile.getPath() + ".tmp");
            Rc4EncryptionUtil.subFile(srcFile, tmpFile, headXml.length() + 16L, srcFile.length());
            // rc4解密
            rc4File = new File(dstFile.getPath() + ".rc4");
            Rc4EncryptionUtil.batchDecrypt(tmpFile, rc4File, encKey, Mode.DECRYPTION);

            // 校验
            validateCheckSum(dstFile, rc4File);

            // 去掉开头16位随机数以及最后的20位校验码，得到最终文件
            Rc4EncryptionUtil.subFile(rc4File, dstFile, 16, rc4File.length() - 20);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failure.", e);
        } finally {
            // 删除中间文件
            FileUtils.deleteQuietly(tmpFile);
            FileUtils.deleteQuietly(rc4File);
        }
    }

    private static void validateCheckSum(File dstFile, File rc4File) throws IOException {
        Rc4EncryptionUtil.subFile(rc4File, dstFile, 0, rc4File.length() - 20);
        String checkSum = null;
        try (InputStream stream = Files.newInputStream(dstFile.toPath())) {
            String checkSumSha1 = DigestUtils.sha1Hex(stream); // NOSONAR
            checkSum = Rc4EncryptionUtil.transHexString(checkSumSha1);
        }

        try (InputStream inputStream = Files.newInputStream(rc4File.toPath())) {
            byte[] checkSumBytes = new byte[20];
            long skip = inputStream.skip(dstFile.length());
            if (skip == -1) {
                throw new RuntimeException("skip failed!");
            }
            int read = inputStream.read(checkSumBytes);
            if (read == -1) {
                throw new RuntimeException("checkSum's length is not right!");
            }
            String checkSumStr = new String(checkSumBytes, StandardCharsets.ISO_8859_1);
            if (!checkSumStr.equals(checkSum)) {
                throw new RuntimeException("checkSum is not consist!");
            }
        }
    }

}
