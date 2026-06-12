package sc.laplace.test.hillstone.dsgp.plugin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * @author jxwu
 */
@Slf4j
public class LibraryUtil {
    private LibraryUtil() {
    }

    /**
     * 生成sig特征库文件
     *
     * @param originFilePath
     * @param sigFilePath
     */
    public static void generateSigFile(Path originFilePath, Path sigFilePath, String psk, String salt) {
        Assert.isTrue(Files.isRegularFile(originFilePath), "originFilePath is not a file");
        Path tarPath = null;
        try {
            tarPath = Files.createTempFile(originFilePath.getParent(), "compressed", ".tar.gz");
            CompressUtil.compressTarGzFile(originFilePath.toFile(), tarPath.toFile());
            EncryptionHead head = new EncryptionHead("DSGP_DS_PLUGIN", "1", "1.0.0", "123456", "psk", "yes", "txt2");
            Rc4Encryptor.encryptFileWithXmlHead(head, psk, salt, tarPath.toFile(), sigFilePath.toFile());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                Files.deleteIfExists(tarPath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 解密解压特征库文件，获取原始文件
     *
     * @param sigFilePath
     * @param targetDirPath
     * @param psk
     * @param salt
     */
    public static void parseSigFile(Path sigFilePath, Path targetDirPath, String psk, String salt) {
        Assert.isTrue(Files.isRegularFile(sigFilePath), "sigFilePath is not a file");
        Assert.isTrue(Files.isDirectory(targetDirPath), "targetDirPath is not a directory");
        Path tarPath = null;
        try {
            String sigFileName = sigFilePath.getFileName().toString();
            tarPath = Files.createTempFile(targetDirPath, sigFileName.substring(0, sigFileName.indexOf(".")), "tar.gz");
            // 解密
            Rc4Decryptor.decryptFile(sigFilePath.toFile(), tarPath.toFile(), psk, salt);

            // 解压
            CompressUtil.decompressTarGzFile(tarPath.toFile(), targetDirPath.toFile());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                Files.deleteIfExists(tarPath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


}
