package sc.laplace.test.util;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Url工具类
 *
 * @author jxwu
 * @version 1.0
 * @date 2021/12/23 14:25
 */
public class UrlUtil {

    private UrlUtil() {}

    /**
     * url中host部分匹配二进制ip
     */
    private static final Pattern PATTERN_IP = Pattern.compile("^[01]{8}(\\.[01]{8}){3}$");

    /**
     * FR22750中对Url格式进行处理
     *
     * @param urlStr urlStr
     * @return java.lang.String
     * @methodName urlProcess
     * @author jxwu
     * @date 2021/12/23 14:38
     **/
    @SuppressWarnings("Duplicates")
    public static String urlProcess(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            return null;
        }
        StringBuilder urlP = new StringBuilder();

        // 添加协议
        urlP.append(url.getProtocol()).append("://");

        // 处理主机名
        if (!StringUtils.isEmpty(url.getHost())) {
            String host = Arrays.stream(url.getHost().split("\\."))
                    .filter(s -> !StringUtils.isEmpty(s))
                    .collect(Collectors.joining("."));

            // ioc若为二进制ip，需转换为十进制（目前只对二进制ip做单独处理）
            Matcher matcher = PATTERN_IP.matcher(host);
            if (matcher.find()) {
                host = Arrays.stream(host.split("\\."))
                        .map(s -> Integer.valueOf(s, 2).toString())
                        .collect(Collectors.joining("."));
            }
            urlP.append(host);
        } else {
            return null;
        }

        // 处理端口
        if (url.getPort() != -1 && url.getDefaultPort() != url.getPort()) {
            urlP.append(":").append(url.getPort());
        }

        // 处理路径
        if (!StringUtils.isEmpty(url.getPath())) {
            Arrays.stream(url.getPath().split("/"))
                    .filter(s -> !StringUtils.isEmpty(s) && !s.contains("."))
                    .forEach(s -> urlP.append("/").append(s));
        }
        return urlP.toString();
    }


    public static boolean isUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
