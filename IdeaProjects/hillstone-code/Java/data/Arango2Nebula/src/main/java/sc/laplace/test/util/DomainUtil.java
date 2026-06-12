package sc.laplace.test.util;

import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * TIP 使用的 domain 相关的统一工具类
 *
 * @author cylv
 * @date 2022/12/28 10:45
 */
public class DomainUtil {
    private static final String DOMAIN_SEPARATOR = ".";
    private static final int SLD_LEN = 2;

    private DomainUtil() {
        // no op
    }

    /**
     * 校验域名是否合法
     *
     * @param domainName 待校验的域名
     * @return java.lang.Boolean true if the domain is illegal, otherwise false
     * @author cylv
     * @date 2022/12/28 10:46
     */
    public static boolean isDomain(String domainName) {
        try {
            InternetDomainName.from(domainName);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * 比较两个域名是否同SLD
     */
    public static boolean isSameSLD(String d1, String d2) {
        return Objects.equals(getSLD(d1), getSLD(d2));
    }

    /**
     * 获取二级域名
     */
    public static String getSLD(String domain) {
        if (StringUtils.isEmpty(domain)) {
            return null;
        }
        InternetDomainName name = InternetDomainName.from(domain);
        if (name.isUnderPublicSuffix()) {
            return name.topPrivateDomain().toString();
        }
        return domain;
    }

    /**
     * 判断是否是复合顶级域名
     */
    public static boolean isKnownTopLevelDomain(String tld) {
        String[] knownTld = {"co.uk", "co.za", "co.nz", "co.in", "co.il", "co.jp", "co.kr", "co.th", "co.id", "co.vn",
                "com.au", "com.br", "com.cn", "com.ec", "com.eg", "com.hk", "com.mx", "com.ng", "com.ph", "com.pk", "com.sa", "com.sg", "com.tr", "com.ve",
                "edu.cn", "edu.au", "edu.mx", "edu.br", "edu.in", "edu.ng", "edu.pk", "edu.sa", "edu.tr", "edu.za",
                "org.uk", "org.nz", "gov.cn"};
        for (String tldDomain : knownTld) {
            if (tldDomain.equalsIgnoreCase(tld)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 规范化Domain，包括
     * <p>
     * 1. 全部变为小写
     * 2. 去掉最后的点（根）
     * 3. 不是合法的 Domain 则返回 null
     */
    public static String formatDomain(String d) {
        if (StringUtils.isEmpty(d) || !isDomain(d)) {
            return null;
        }
        if (d.endsWith(DOMAIN_SEPARATOR)) {
            d = d.substring(0, d.length() - 1);
        }
        return d.toLowerCase();
    }
}
