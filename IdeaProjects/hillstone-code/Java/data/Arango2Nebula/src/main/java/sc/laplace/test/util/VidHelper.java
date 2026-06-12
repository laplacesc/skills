package sc.laplace.test.util;

import lombok.experimental.UtilityClass;

/**
 * 统一维护 Nebula 顶点 VID 的原始 key 约定。
 * 各类型 key 统一加前缀后再做 sha256，避免跨类型值冲突。
 */
@UtilityClass
public class VidHelper {
    private static final String PREFIX_IPV4 = "ipv4:";
    private static final String PREFIX_DOMAIN = "domain:";
    private static final String PREFIX_URL = "url:";
    private static final String PREFIX_FILE = "file:";
    private static final String PREFIX_TAG = "tag:";
    private static final String PREFIX_HASH = "hash:";

    public static String vidFromIpv4(String ipAddress) {
        return DigestHelper.sha256Hex(ipv4Key(ipAddress));
    }

    public static String vidFromDomain(String domainName) {
        return DigestHelper.sha256Hex(domainKey(domainName));
    }

    public static String vidFromUrl(String url) {
        return DigestHelper.sha256Hex(urlKey(url));
    }

    public static String vidFromFileHash(String hash) {
        return DigestHelper.sha256Hex(fileKey(hash));
    }

    public static String vidFromTagId(Long id) {
        return DigestHelper.sha256Hex(tagKey(id));
    }

    public static String vidFromHash(String hash) {
        String normalized = normalize(hash);
        if (normalized != null && normalized.startsWith(PREFIX_HASH)) {
            return DigestHelper.sha256Hex(normalized);
        }
        return DigestHelper.sha256Hex(hashKey(hash));
    }

    public static String ipv4Key(String ipAddress) {
        String normalized = normalize(ipAddress);
        return normalized != null && normalized.startsWith(PREFIX_IPV4) ? normalized : PREFIX_IPV4 + normalized;
    }

    public static String domainKey(String domainName) {
        String normalized = normalize(domainName);
        return normalized != null && normalized.startsWith(PREFIX_DOMAIN) ? normalized : PREFIX_DOMAIN + normalized;
    }

    public static String urlKey(String url) {
        String normalized = normalize(url);
        return normalized != null && normalized.startsWith(PREFIX_URL) ? normalized : PREFIX_URL + normalized;
    }

    public static String fileKey(String hash) {
        String normalized = normalize(hash);
        return normalized != null && normalized.startsWith(PREFIX_FILE) ? normalized : PREFIX_FILE + normalized;
    }

    public static String tagKey(Long id) {
        return PREFIX_TAG + String.valueOf(id);
    }

    public static String hashKey(String hash) {
        String normalized = normalize(hash);
        return normalized != null && normalized.startsWith(PREFIX_HASH) ? normalized : PREFIX_HASH + normalized;
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
