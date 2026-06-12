package sc.laplace.test.hillstone.dsgp.plugin;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;


public class LibraryUtilTest {
    public static final String PLUGIN_PSK = "hillStone123Qwe-DsgpDataSourcePlugin";
    public static final String PLUGIN_SALT = "DsgpDataSourcePlugin";
    Path src = Paths.get("/Users/jxwu/Downloads");
    Path dst = Paths.get("/Users/jxwu/Downloads");

    @Test
    public void generateSigFile() {
        LibraryUtil.generateSigFile(src.resolve("ds-plugin-impl-redis.jar"), dst.resolve("ds-plugin-impl-redis.sig"), PLUGIN_PSK, PLUGIN_SALT);
        // LibraryUtil.generateSigFile(src.resolve("ds-plugin-impl-ftp.jar"), dst.resolve("ds-plugin-impl-ftp.sig"), PLUGIN_PSK, PLUGIN_SALT);
        // LibraryUtil.generateSigFile(src.resolve("ds-plugin-impl-shentong.jar"), dst.resolve("ds-plugin-impl-shentong.sig"), PLUGIN_PSK, PLUGIN_SALT);
    }
}