package sc.laplace.test.hillstone.dsgp.plugin;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.*;


/**
 * @author jxwu
 */
public class CompressUtil {

    private CompressUtil() {
    }

    /**
     * 压缩文件/目录为tar.gz
     *
     * @param input
     * @param output
     * @throws java.io.IOException
     */
    public static void compressTarGzFile(File input, File output) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(output);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(bos);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(gcos)) {

            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            addFileToTarGz(taos, input.getAbsolutePath(), "");
        }
    }

    private static void addFileToTarGz(TarArchiveOutputStream taos, String path, String base) throws IOException {
        File file = new File(path);
        String entryName = base + file.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);

        taos.putArchiveEntry(tarEntry);

        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    taos.write(buffer, 0, len);
                }
            }
            taos.closeArchiveEntry();
        } else {
            taos.closeArchiveEntry();
            // 如果是目录，递归处理目录下的文件
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToTarGz(taos, child.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }

    public static void decompressTarGzFile(File input, File outputDir) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs(); // 确保输出目录存在
        }

        try (FileInputStream fis = new FileInputStream(input);
             GzipCompressorInputStream gcis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gcis)) {

            TarArchiveEntry entry;
            // 遍历.tar文件中的每个条目
            while ((entry = tais.getNextEntry()) != null) {
                File outputFile = new File(outputDir, entry.getName());

                if (entry.isDirectory()) {
                    // 如果条目是目录，则创建目录
                    if (!outputFile.exists()) {
                        outputFile.mkdirs();
                    }
                } else {
                    // 如果条目是文件，则写出文件内容
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = tais.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }
}
