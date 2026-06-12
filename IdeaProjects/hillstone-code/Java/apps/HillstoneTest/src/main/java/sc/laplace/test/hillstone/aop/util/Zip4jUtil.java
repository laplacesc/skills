package sc.laplace.test.hillstone.aop.util;

import lombok.experimental.UtilityClass;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * @author jxwu
 */
@UtilityClass
public class Zip4jUtil {

    public static void compressFiles2Zip(List<Path> srcPaths, Path zipPath) throws IOException {
        compressFiles2Zip(srcPaths, zipPath, null);
    }

    public static void compressFiles2Zip(List<Path> srcPaths, Path zipPath, String password) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            if (password != null) {
                zipFile.setPassword(password.toCharArray());
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            }
            for (Path path : srcPaths) {
                if (path.toFile().isDirectory()) {
                    zipFile.addFolder(path.toFile(), zipParameters);
                } else {
                    zipFile.addFile(path.toFile(), zipParameters);
                }
            }
        }
    }

    public static InputStream compressFiles2InputStream(List<InputStream> isList, List<String> nameList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(baos)) {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            for (int i = 0; i < isList.size(); i++) {
                zipParameters.setFileNameInZip(nameList.get(i));
                zipOutputStream.putNextEntry(zipParameters);
                try (InputStream inputStream = isList.get(i)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                }
                zipOutputStream.closeEntry();
            }
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static void decompressSpecifiedFile(Path zipFilePath, Path targetFilePath, Path targetDirPath) throws ZipException {
        decompressSpecifiedFile(zipFilePath, targetFilePath, targetDirPath, null);
    }

    public static void decompressSpecifiedFile(Path zipFilePath, Path targetFilePath, Path targetDirPath, String password) throws ZipException {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            if (zipFile.isEncrypted() && password != null) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractFile(targetFilePath.toString(), targetDirPath.toString());
        } catch (IOException e) {
            throw new ZipException(e.getMessage(), e);
        }
    }

    public static void decompressFiles(Path zipFilePath, Path targetDirPath) throws ZipException {
        decompressFiles(zipFilePath, targetDirPath, null);
    }

    public static void decompressFiles(Path zipFilePath, Path targetDirPath, String password) throws ZipException {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            if (zipFile.isEncrypted() && password != null) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(targetDirPath.toString());
        } catch (Exception e) {
            throw new ZipException(e.getMessage(), e);
        }
    }
}
