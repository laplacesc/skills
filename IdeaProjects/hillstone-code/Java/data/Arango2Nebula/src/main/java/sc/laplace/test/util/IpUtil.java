package sc.laplace.test.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;


/**
 * @author jxwu
 */
public class IpUtil {

    public static final String IP_TYPE_V4 = "IPv4";
    public static final String IP_TYPE_V6 = "IPv6";
    private static final InetAddressValidator VALIDATOR = InetAddressValidator.getInstance();
    private static final List<Pair<Long, Integer>> PRIVATE_IP_2_MASK;
    private static final List<Pair<Long, Integer>> RESERVED_IP_2_MASK;
    private static final Integer IP_LENGTH = 32;
    private static final String IP_PORT_REGEX = "^(?:[0-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";

    static {
        List<String> privateIp = new ArrayList<>();
        privateIp.add("10.0.0.0/8");
        privateIp.add("100.64.0.0/10");
        privateIp.add("172.16.0.0/12");
        privateIp.add("192.168.0.0/16");
        privateIp.add("198.18.0.0/15");
        privateIp.add("192.0.0.0/24");
        List<String> reservedIp = new ArrayList<>();
        reservedIp.add("0.0.0.0/8");
        reservedIp.add("127.0.0.0/8");
        reservedIp.add("169.254.0.0/16");
        reservedIp.add("224.0.0.0/4");
        reservedIp.add("240.0.0.0/4");
        reservedIp.add("255.255.255.255/32");
        reservedIp.add("192.0.2.0/24");
        reservedIp.add("198.51.100.0/24");
        reservedIp.add("203.0.113.0/24");


        reservedIp.add("192.88.99.0/24");

        PRIVATE_IP_2_MASK = privateIp.stream()
                .map(ip -> Pair.of(ip2Long(ip.split("/")[0]),
                        0xFFFFFFFF << (IP_LENGTH - Long.parseLong(ip.split("/")[1]))))
                .collect(Collectors.toList());
        RESERVED_IP_2_MASK = reservedIp.stream()
                .map(ip -> Pair.of(ip2Long(ip.split("/")[0]),
                        0xFFFFFFFF << (IP_LENGTH - Long.parseLong(ip.split("/")[1]))))
                .collect(Collectors.toList());
    }

    private IpUtil() {
    }

    /**
     * 判断一个ip地址是否为私有地址
     */
    public static Boolean isPrivateAddress(String ipAddress) {
        Long ip = ip2Long(ipAddress);
        return ip != -1L && PRIVATE_IP_2_MASK.stream().anyMatch(pair -> pair.getLeft() == (ip & pair.getRight()));
    }

    /**
     * 判断一个ip地址是否为保留地址
     */
    public static Boolean isReservedAddress(String ipAddress) {
        Long ip = ip2Long(ipAddress);
        return ip != -1L && RESERVED_IP_2_MASK.stream().anyMatch(pair -> pair.getLeft() == (ip & pair.getRight()));
    }

    /**
     * 将ip转为数字
     */
    public static Long ip2Long(String ipAddress) {
        if (!isIpv4(ipAddress)) {
            return -1L;
        }
        return Arrays.stream(ipAddress.split("\\."))
                .map(Long::parseLong)
                .reduce(0L, (subtotal, element) -> subtotal << 8 | element);
    }

    /**
     * 将数字转为ip
     */
    public static String long2Ip(Long ipLong) {
        if (ipLong == null) {
            return null;
        }
        List<Long> ips = new ArrayList<>();
        LongStream.range(0, 4).reduce(ipLong, (result, element) -> {
            ips.add(result % 256L);
            return result / 256;
        });
        Collections.reverse(ips);
        return ips.stream().map(String::valueOf).collect(Collectors.joining("."));
    }

    /**
     * 判断一个IP是不是在一个网段下的
     * isInRange("192.168.8.3", "192.168.9.10/22");
     */
    public static boolean isInRange(String ipAddress, String cidrAddress) {
        Long ip = ip2Long(ipAddress);
        Long cidr = ip2Long(cidrAddress.split("/")[0]);
        Integer mask = 0xFFFFFFFF << (IP_LENGTH - Long.parseLong(cidrAddress.split("/")[1]));
        return ip != -1L && cidr != -1L && cidr == (ip & mask);
    }

    /**
     * 返回当前ip的下一个ip地址
     */
    public static String nextIp(String ipAddress) {
        if (!isIpv4(ipAddress)) {
            return null;
        }
        List<Integer> currentIps = Arrays.stream(ipAddress.split("\\."))
                .map(Integer::parseInt).collect(Collectors.toList());
        Collections.reverse(currentIps);
        List<Integer> nextIps = new ArrayList<>();
        currentIps.stream().reduce(1, (result, element) -> {
            nextIps.add((element + result) % 256);
            return (element + result) / 256;
        });
        Collections.reverse(nextIps);
        return nextIps.stream().map(String::valueOf).collect(Collectors.joining("."));
    }

    /**
     * 比较两个ip地址的大小
     * 1：第一个ip地址大
     * 0：相等
     * -1：第二个ip地址大
     */
    public static int compareIp(String first, String second) {
        return ip2Long(first).compareTo(ip2Long(second));
    }

    /**
     * 返回两个ip地址里小的那个ip
     */
    public static String minIp(String ip1, String ip2) {
        return compareIp(ip1, ip2) < 0 ? ip1 : ip2;
    }

    /**
     * 返回两个ip地址里大的那个ip
     */
    public static String maxIp(String ip1, String ip2) {
        return compareIp(ip1, ip2) > 0 ? ip1 : ip2;
    }

    public static boolean isIp(String str) {
        return isIpv4(str) || isIpv6(str);
    }

    public static boolean isIpv4(String ipAddress) {
        return VALIDATOR.isValidInet4Address(ipAddress);
    }

    public static boolean isIpv6(String ipAddress) {
        return VALIDATOR.isValidInet6Address(ipAddress);
    }

    public static boolean isIpPort(String port) {
        return StringUtils.isNotEmpty(port) && port.matches(IP_PORT_REGEX);
    }

    public static boolean isIpPort(Integer port) {
        return port != null && String.valueOf(port).matches(IP_PORT_REGEX);
    }

    public static String getIpType(String ip) {
        if (isIpv4(ip)) {
            return IP_TYPE_V4;
        }
        if (isIpv6(ip)) {
            return IP_TYPE_V6;
        }
        return null;
    }

}
