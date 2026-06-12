package com.hillstone.simulator.utils;

import org.apache.commons.lang.math.RandomUtils;

/**
 *
 */
public class NameMocker {
    public NameMocker(){}
    public static final String[] SN_LIST = {"9999956789012346", "8888880123456789", "7777770123456789", "abcdef0123456789",
            "abcdef9876543210"};
    public static final String[] VSYSNAMES = {"root", "vsys_00", "vsys_01", "vsys_02", "vsys_03", "vsys_04"};

    public static final String[] ZONENAMES = {"trust", "untrust", "dmz", "l2-trust", "l2-untrust", "l2-dmz"};

    public static final String[] IFNAMES = {"ethernet0/0", "ethernet0/1", "ethernet0/3", "ethernet0/4", "ethernet0/5"};
    public static final String[] IPNAMES = {"192.168.0.1", "192.168.0.2", "192.168.1.1", "192.168.2.1", "192.168.2.2"};
    public static final String[] APPNAMES = {"APP1", "APP2", "APP3", "APP4"};

    public static long getLong() {

        int choice = RandomUtils.nextInt(5);
        long l = 0L;
        if (choice < 3) {
            l = RandomUtils.nextInt(1000) * 10L;
        } else if (choice == 3) {
            l = RandomUtils.nextInt(1000) * 100L;
        } else if (choice == 4) {
            l = RandomUtils.nextInt(1000) * 1000000L;
        }

        return l;
    }

    public static int getInt() {
        return RandomUtils.nextInt(Integer.MAX_VALUE);

    }

    public static int getInt(int n) {
        return RandomUtils.nextInt(n);
    }

    public static double getDouble() {
        return RandomUtils.nextDouble() * 100D;
    }


    public static String getZoneName() {
        return ZONENAMES[RandomUtils.nextInt(ZONENAMES.length)];

    }

    public static String getIfName() {
        return IFNAMES[(RandomUtils.nextInt(IFNAMES.length))];
    }

    public static String getVsysName() {
        return VSYSNAMES[RandomUtils.nextInt(VSYSNAMES.length)];
    }

}
