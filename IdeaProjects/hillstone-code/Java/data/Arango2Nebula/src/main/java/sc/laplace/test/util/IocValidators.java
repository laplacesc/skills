package sc.laplace.test.util;

import lombok.experimental.UtilityClass;
import sc.laplace.test.model.vertex.*;

import java.util.regex.Pattern;

@UtilityClass
/**
 * 迁移前的 IOC 基础合法性校验。
 *
 * <p>目标不是做完整业务校验，而是尽早拦掉会导致 Nebula 顶点无效或不可定位的脏数据，
 * 比如非法 IP、非法域名、无任何有效 hash 的文件。
 */
public class IocValidators {
    private static final Pattern MD5_PATTERN = Pattern.compile("^[a-fA-F0-9]{32}$");
    private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    private static final Pattern SHA256_PATTERN = Pattern.compile("^[a-fA-F0-9]{64}$");
    private static final Pattern SHA512_PATTERN = Pattern.compile("^[a-fA-F0-9]{128}$");

    /**
     * 按 collection 选择对应的顶点校验规则。
     */
    public static boolean isValidVertex(String collection, Vertex vertex) {
        if (collection == null || vertex == null) {
            return false;
        }

        String normalized = collection.trim().toLowerCase();
        if ("ipv4".equals(normalized)) {
            return vertex instanceof Ipv4 && isValidIpv4((Ipv4) vertex);
        }
        if ("domain".equals(normalized)) {
            return vertex instanceof Domain && isValidDomain((Domain) vertex);
        }
        if ("url".equals(normalized)) {
            return vertex instanceof Url && isValidUrl((Url) vertex);
        }
        if ("file".equals(normalized)) {
            return vertex instanceof File && isValidFile((File) vertex);
        }
        return true;
    }

    public static boolean isValidIpv4(Ipv4 ipv4) {
        return ipv4 != null && isIpv4(ipv4.getIpAddress());
    }

    public static boolean isValidDomain(Domain domain) {
        return domain != null && isDomain(domain.getDomainName());
    }

    public static boolean isValidUrl(Url url) {
        return url != null && isUrl(url.getUrl());
    }

    public static boolean isValidFile(File file) {
        if (file == null) {
            return false;
        }

        // file 顶点最少要有一个合法 hash，否则既无法生成稳定 VID，也无法建立 hash_of_file 关系。
        boolean hasHash = false;
        if (file.getHashMd5() != null) {
            hasHash = true;
            if (!isMd5(file.getHashMd5())) {
                return false;
            }
        }
        if (file.getHashSha1() != null) {
            hasHash = true;
            if (!isSha1(file.getHashSha1())) {
                return false;
            }
        }
        if (file.getHashSha256() != null) {
            hasHash = true;
            if (!isSha256(file.getHashSha256())) {
                return false;
            }
        }
        if (file.getHashSha512() != null) {
            hasHash = true;
            if (!isSha512(file.getHashSha512())) {
                return false;
            }
        }
        return hasHash;
    }

    public static boolean isIpv4(String ipAddress) {
        return IpUtil.isIpv4(ipAddress);
    }

    public static boolean isDomain(String domainName) {
        return DomainUtil.isDomain(domainName);
    }

    public static boolean isUrl(String url) {
        return UrlUtil.isUrl(url);
    }

    public static boolean isMd5(String hash) {
        return matches(MD5_PATTERN, hash);
    }

    public static boolean isSha1(String hash) {
        return matches(SHA1_PATTERN, hash);
    }

    public static boolean isSha256(String hash) {
        return matches(SHA256_PATTERN, hash);
    }

    public static boolean isSha512(String hash) {
        return matches(SHA512_PATTERN, hash);
    }

    public static boolean isValidHash(String hashField, String hashValue) {
        if ("hash_md5".equals(hashField)) {
            return isMd5(hashValue);
        }
        if ("hash_sha1".equals(hashField)) {
            return isSha1(hashValue);
        }
        if ("hash_sha256".equals(hashField)) {
            return isSha256(hashValue);
        }
        if ("hash_sha512".equals(hashField)) {
            return isSha512(hashValue);
        }
        return false;
    }

    private static boolean matches(Pattern pattern, String value) {
        return value != null && pattern.matcher(value.trim()).matches();
    }
}
