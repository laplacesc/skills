package sc.laplace.test.hillstone.dsgp.plugin;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


/**
 * @author jxwu
 */
public class Rc4EncryptionUtil {
    private Rc4EncryptionUtil() {
    }

    /**
     * 获取加密文件头的xml
     */
    public static String getHeadXml(File file) throws IOException {
        StringBuilder headXml = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int ch = 0;
            char[] chars = new char[5];
            reader.read(chars);
            if (String.copyValueOf(chars).startsWith("<xml>")) {
                headXml.append(chars);
                while ((ch = reader.read()) != -1) {
                    headXml.append((char) ch);
                    if (headXml.lastIndexOf("</xml>") > 0) {
                        break;
                    }
                }
            }
        }
        return headXml.toString();
    }

    /**
     * 转化16进制字符串，每两位转化成一个字符
     */
    protected static String transHexString(String str) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < str.length(); i += 2) {
            res.append((char) Integer.parseInt(str.substring(i, i + 2), 16));
        }
        return res.toString();
    }

    /**
     * 进行Base64解密，并将负数加上256
     */
    protected static char[] decryptByBase64(String data) {
        return bytesToChars(Base64.getDecoder().decode(data.getBytes()));
    }

    /**
     * 读取加密文件开头的随机数
     *
     * @param file 待解密文件
     * @param from 开始位置
     * @param len  长度
     * @return char[]
     */
    protected static char[] readRandomNumber(File file, int from, int len) throws IOException {
        try (RandomAccessFile fileToRead = new RandomAccessFile(file, "r")) {
            byte[] readBytes = new byte[(int) Math.min(len, fileToRead.length() - from)];
            fileToRead.seek(from);
            fileToRead.read(readBytes, 0, len);
            return bytesToChars(readBytes);
        }
    }

    /**
     * 分割原文件成新文件
     */
    protected static void subFile(File srcFile, File dstFile, long start, long end) throws IOException {
        if (end - start > Integer.MAX_VALUE) {
            throw new RuntimeException("File " + srcFile.getPath() + " is too large");
        }

        if (dstFile.exists()) {
            FileUtils.writeStringToFile(dstFile, "", StandardCharsets.UTF_8);
        }

        int fileSize = (int) (end - start);
        int perlen = 32768;
        if (fileSize <= perlen) {
            perlen = fileSize;
        }
        byte[] bytes = new byte[perlen];

        try (FileInputStream fis = new FileInputStream(srcFile)) {
            if (fis.skip(start) != start) {
                throw new RuntimeException("File input stream skip error");
            }
            int sumlen = 0;
            int tmplen = -1;
            while ((tmplen = fis.read(bytes)) > 0) {
                if (tmplen >= (fileSize - sumlen)) {
                    FileUtils.writeByteArrayToFile(dstFile, bytes, 0, fileSize - sumlen, true);
                    break;
                }
                FileUtils.writeByteArrayToFile(dstFile, bytes, true);
                sumlen += tmplen;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 对rc4加密文件进行分批加解密
     */
    protected static void batchDecrypt(File srcFile, File dstFile, String encKey, Mode mode) throws IOException {
        if (dstFile.exists() && Mode.DECRYPTION.equals(mode)) {
            FileUtils.writeStringToFile(dstFile, "", StandardCharsets.UTF_8);
        }
        int batchSize = 1048576;
        MagicTool magicTool = new MagicTool(encKey);
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            byte[] buffer = new byte[batchSize];
            int len = -1;
            while ((len = fis.read(buffer)) > 0) {
                byte[] resultBytes = magicTool.rc4(buffer, 0, len);
                FileUtils.writeByteArrayToFile(dstFile, resultBytes, 0, len, true);
            }
        }
    }

    protected static void batchWriteBytes(File srcFile, File dstFile) throws IOException {
        int batchSize = 104857600;
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            byte[] buffer = new byte[batchSize];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                FileUtils.writeByteArrayToFile(dstFile, buffer, 0, len, true);
            }
        }
    }

    /**
     * rc4算法加解密
     *
     * @param aInput    待加解密字符串
     * @param publicKey 密钥
     * @return 加解密结果
     */
    protected static String rc4(String aInput, String publicKey) {
        int keyLen = publicKey.length();
        int strLen = aInput.length();
        int[] sbox = new int[256];
        for (int i = 0; i < 256; i++) {
            sbox[i] = i;
        }

        int j = 0;
        int[] k = new int[256];
        for (int i = 0; i < 256; i++) {
            k[i] = publicKey.charAt(i % keyLen);
        }

        for (int i = 0; i < 256; i++) {
            j = (j + sbox[i] + k[i]) % 256;
            int tmp = sbox[i];
            sbox[i] = sbox[j];
            sbox[j] = tmp;
        }

        int i = 0;
        j = 0;
        int t = 0;
        char[] resultChar = new char[strLen];
        for (int kk = 0; kk < strLen; kk++) {
            i = (i + 1) % 256;
            j = (j + sbox[i]) % 256;
            int tmp = sbox[i];
            sbox[i] = sbox[j];
            sbox[j] = tmp;
            t = (sbox[i] + sbox[j]) % 256;
            resultChar[kk] = (char) (aInput.charAt(kk) ^ sbox[t]);
        }
        return new String(resultChar);
    }

    protected static char[] bytesToChars(byte[] bytes) {
        char[] res = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            res[i] = bytes[i] >= 0 ? (char) bytes[i] : (char) (bytes[i] + 256);
        }
        return res;
    }

    protected static String enhancedBase64(String str) {
        char[] c1 = str.toCharArray();
        int length = c1.length;
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) c1[i];
        }
        return new String(Base64.getEncoder().encode(bytes));
    }


}
