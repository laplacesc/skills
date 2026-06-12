package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.mocker.IAvroMocker;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.lang.math.RandomUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:51
 * @description: some desc
 */
public class ThreatEventMocker extends IAvroMocker {

    protected static final String[] VSYSNAMES = {"root", "vsys_00", "vsys_01", "vsys_02", "vsys_03", "vsys_04"};

    protected static final String[] ZONENAMES = {"trust", "untrust", "dmz", "l2-trust", "l2-untrust", "l2-dmz"};
    protected static final String[] IFNAMES = {"ethernet0/0", "ethernet0/1", "ethernet0/3", "ethernet0/4", "ethernet0/5"};

    protected static int getInt() {
        return RandomUtils.nextInt(Integer.MAX_VALUE);

    }

    protected static final String[] ABNORMALDNSDOMAINS = {
            "awjl.com",
            "axdm.com",
            "ayes.com"
    };

    protected static final String[] SuspiciousDNSDomains = {
            "bape.com",
            "baso.com",
            "bavu.com"
    };

    protected static final String[] DGADNSDomains = {
            "jd.com",
            "cctv.com",
            "qq.com"
    };

    /**
     * "221.224.30.130", "114.114.114.114", "198.87.65.23", "12.88.89.23",
     * "21.87.65.90"
     */
    protected static final String ipString[] = {"172.16.139.100", "172.16.139.80", "198.87.65.23", "12.88.89.23",
            "21.87.65.90"};

    protected static final String ipHostName[] = {"tianxia", "fengyun", "huoying", "haizeiwang", "baiyexing"};

    protected static final String DEFENDER_ID = "defender_id";
    protected static final String PRI_TYPE = "pri_type";
    protected static final String SEC_TYPE = "sec_type";
    protected static final String APP_ID = "app_id";
    protected static final String APP_NAME = "app_name";
    protected static final String PROTOCOL = "protocol";
    protected static final String AGGREGATION_ID = "aggregation_id";


    public ThreatEventMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.THREAT_EVENT_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.THREAT_EVENT_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.THREAT_EVENT_AVRO_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.THREAT_EVENT_MD5;
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(this.getAvroFilePath());
        Schema schema = new Schema.Parser().parse(ins);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);

        // endTime
        long curTimestamp = getCurTimestamp() / 1000;
        int num = 10;
        if ("yes".equalsIgnoreCase(AvroConstant.SPEED)) {
            num = 30;
        }
        for (int i = 0; i < num; i++) {
            try {
                GenericRecord threatEvent = new GenericData.Record(schema);
                threatEvent.put("id", RandomUtils.nextLong() + 1);
                threatEvent.put("flag_mask", RandomUtils.nextLong() + 1);
                if (i % 3 == 0) {
                    threatEvent.put(DEFENDER_ID, 5);
                } else {
                    threatEvent.put(DEFENDER_ID, 3);
                }
                threatEvent.put(PRI_TYPE, RandomUtils.nextInt(6) + 1);
                threatEvent.put(SEC_TYPE, getSecType((int) threatEvent.get(PRI_TYPE)));
                threatEvent.put("action_id", RandomUtils.nextInt(12));
                threatEvent.put("begin_time", getTimestamp(curTimestamp) / 1000);
                threatEvent.put("end_time", curTimestamp);
                threatEvent.put("confidence", RandomUtils.nextInt(4));
                String[] app = getApp();
                threatEvent.put(APP_ID, Integer.parseInt(app[0]));
                threatEvent.put(APP_NAME, app[1]);
                threatEvent.put(PROTOCOL, RandomUtils.nextInt(5) + 1);
                threatEvent.put("policy_id", RandomUtils.nextInt(5) + 1);
                threatEvent.put("profile_id", RandomUtils.nextInt(5) + 1);
                threatEvent.put("src_vsysid", RandomUtils.nextInt(2));
                threatEvent.put("src_vsysname", VSYSNAMES[RandomUtils.nextInt(VSYSNAMES.length)]);
                threatEvent.put("src_vrid", RandomUtils.nextInt(5));
                threatEvent.put("src_vrname", "vrname" + RandomUtils.nextInt(10));
                threatEvent.put("src_interfaceid", RandomUtils.nextInt(9) + 7);
                threatEvent.put("src_interfacename",
                        IFNAMES[RandomUtils.nextInt(IFNAMES.length)]);
                threatEvent.put("src_zoneid", RandomUtils.nextInt(7) + 1);
                threatEvent.put("src_zonename", ZONENAMES[RandomUtils.nextInt(ZONENAMES.length)]);
                threatEvent.put("src_ip", ipString[(short) RandomUtils.nextInt(5)]);
                threatEvent.put("src_ip_mask_len", RandomUtils.nextInt(32));
                threatEvent.put("src_port", RandomUtils.nextInt(20) + 1);
                threatEvent.put("src_hostindex", RandomUtils.nextLong() + 1);
                threatEvent.put("src_hostname", ipHostName[(short) RandomUtils.nextInt(5)]);
                threatEvent.put("src_country", "中国");
                threatEvent.put("src_region", "江苏");
                threatEvent.put("src_city", "苏州");
                threatEvent.put("dst_vsysid", RandomUtils.nextInt(2));
                threatEvent.put("dst_vsysname", VSYSNAMES[RandomUtils.nextInt(VSYSNAMES.length)]);
                threatEvent.put("dst_vrid", RandomUtils.nextInt(5) + 1);
                threatEvent.put("dst_vrname", "vrname" + RandomUtils.nextInt(5));
                threatEvent.put("dst_interfaceid", RandomUtils.nextInt(9) + 7);
                threatEvent.put("dst_interfacename",
                        IFNAMES[RandomUtils.nextInt(IFNAMES.length)]);
                threatEvent.put("dst_zoneid", RandomUtils.nextInt(7) + 1);
                threatEvent.put("dst_zonename", ZONENAMES[RandomUtils.nextInt(ZONENAMES.length)]);
                threatEvent.put("dst_ip", ipString[(short) RandomUtils.nextInt(5)]);
                threatEvent.put("dst_ip_mask_len", RandomUtils.nextInt(32));
                threatEvent.put("dst_port", RandomUtils.nextInt(100) + 1);
                threatEvent.put("dst_hostindex", RandomUtils.nextLong() + 1);
                threatEvent.put("dst_hostname", ipHostName[(short) RandomUtils.nextInt(5)]);
                threatEvent.put("dst_country", "中国");
                threatEvent.put("dst_region", "北京");
                threatEvent.put("dst_city", "北京");
                threatEvent.put("stage_id", 0);
                threatEvent.put("count", 10);
                threatEvent.put("attacksource", "0");
                String privData = "";
                String eventName = "";
                switch ((int) threatEvent.get(DEFENDER_ID)) {
                    case 1:
                        eventName = "DB22 Oracle MySQL GRANT Command Stack Buffer Overflow (CVE-2012-5611)";
                        privData = privData + "type=threat_profile value=predef_default; type=threat_id value=105243";
                        if (AvroConstant.SPEED.equalsIgnoreCase("true")
                                || AvroConstant.AGGREGATION.equalsIgnoreCase("no") || RandomUtils.nextBoolean()) {
                            threatEvent.put(AGGREGATION_ID, 0L);
                        } else {
                            threatEvent.put(AGGREGATION_ID, 2001L);
                        }
                        break;
                    case 2:
                        eventName = "udp-flood";
                        privData = privData
                                + "type=attack_num value=1; type=zone value=trust; type=alarm_message value=UDP flood attack";
                        threatEvent.put(AGGREGATION_ID, 0L);
                        break;
                    case 3:
                        eventName = "Suspicious SMB Activities";
                        privData = "type=bhv_rule_id value=220; type=pa_alert_uid value=3281;";
                        threatEvent.put(SEC_TYPE, 13);
                        threatEvent.put(PRI_TYPE, 6);
                        threatEvent.put(PROTOCOL, 144);
                        threatEvent.put(APP_NAME, "SMB");
                        if (AvroConstant.SPEED.equalsIgnoreCase("true")
                                || AvroConstant.AGGREGATION.equalsIgnoreCase("no") || RandomUtils.nextBoolean()) {
                            threatEvent.put(AGGREGATION_ID, 0L);
                        } else {
                            threatEvent.put(AGGREGATION_ID, 3001L);
                        }
                        break;
                    case 4:
                        eventName = "unknow";
                        privData = privData
                                + "type=low_reputation_IP value=172.16.1.2; type=reputation_index value=0; type=hit_count value=1; type=pcap_id value=0";
                        threatEvent.put(AGGREGATION_ID, 0L);
                        break;
                    case 5:
                        int choice = RandomUtils.nextInt(3);
                        String[] domain = {"Abnormal DNS Response", "Suspicious Amount Of DNS NXDOMAIN Responses",
                                "The Domain Name of DNS Response Is Malicious Domain Generated by DGA"};
                        Map<String, String[]> threatDomainMap = new HashMap<>();
                        threatDomainMap.put(domain[0], ABNORMALDNSDOMAINS);
                        threatDomainMap.put(domain[1], SuspiciousDNSDomains);
                        threatDomainMap.put(domain[2], DGADNSDomains);
                        if (choice == 1) {
                            eventName = "Suspicious PE (executable) File Download";
                            privData = "type=bhv_rule_id value=201; type=pa_alert_uid value=24043062;"
                                    + " type=file_md5 value=" + "9433a847f2dcbb6be99a70411e563b9b"
                                    + "; type=file_size value=14842;" + " type=file_type value=pe;"
                                    + " type=uri value=http://i.kpzip.com/n/tui/mininews/mininewsxktt/v4.1.2.1/mininewsxktt-1.exe";

                            threatEvent.put(PRI_TYPE, 2);
                            threatEvent.put(SEC_TYPE, 28);
                            threatEvent.put(PROTOCOL, 255);
                            threatEvent.put(APP_ID, 0);
                            threatEvent.put(APP_NAME, "file");
                        } else {
                            eventName = domain[RandomUtils.nextInt(domain.length)];
                            String domanName = "";
                            switch (eventName) {
                                case "Abnormal DNS Response":
                                    threatEvent.put(PRI_TYPE, 6);
                                    threatEvent.put(SEC_TYPE, 17);
                                    domanName = threatDomainMap.get(eventName)[RandomUtils
                                            .nextInt(ABNORMALDNSDOMAINS.length)];
                                    privData = "type=DOMAIN value=" + domanName + "; type=pcap_id value=299523761718065072;"
                                            + " type=DNSSERVER value=8.8.8.8; type=bhv_rule_id value=30002";
                                    break;

                                case "Suspicious Amount Of DNS NXDOMAIN Responses":
                                    threatEvent.put(PRI_TYPE, 2);
                                    threatEvent.put(SEC_TYPE, 27);
                                    domanName = threatDomainMap.get(eventName)[RandomUtils
                                            .nextInt(SuspiciousDNSDomains.length)];
                                    privData = "type=bhv_rule_id value=207; type=pa_alert_uid value=2140;"
                                            + " type=DNSSERVER value=8.8.8.8; type=DOMAIN value=" + domanName + ";"
                                            + " type=pcap_id value=297253312166627982;";

                                    break;

                                case "The Domain Name of DNS Response Is Malicious Domain Generated by DGA":
                                    threatEvent.put(PRI_TYPE, 2);
                                    threatEvent.put(SEC_TYPE, 27);

                                    domanName = threatDomainMap.get(eventName)[RandomUtils
                                            .nextInt(DGADNSDomains.length)];
                                    privData = "value=type=DOMAIN value=" + domanName + ";"
                                            + " type=pcap_id value=297253243447150821;"
                                            + " type=DNSSERVER value=8.8.8.8; type=bhv_rule_id value=30002";
                                    break;

                                default:
                                    break;
                            }

                            threatEvent.put(PROTOCOL, 17);
                            threatEvent.put(APP_ID, 11);
                            threatEvent.put(APP_NAME, "DNS");
                        }
                        break;
                    case 6:
                        eventName = "unknow";
                        privData = privData + "type=malware_report_uid value=45";
                        break;
                    default:
                        break;
                }
                if (threatEvent.get(AGGREGATION_ID) == null) {
                    threatEvent.put(AGGREGATION_ID, 0L);

                }
                threatEvent.put("event_name", eventName);
                threatEvent.put("priv_data", privData);
                threatEvent.put("severity", (eventName.length() % 4) + 1);

                datumWriter.write(threatEvent, encoder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        encoder.flush();
        output.close();
        return output.toByteArray();
    }

    private long getTimestamp(long endTime) {
        return endTime * 1000 - RandomUtils.nextInt(10) * 60 * 1000L;
    }

    private long getCurTimestamp() {
        return System.currentTimeMillis();
    }

    private String[] getApp() {
        String[][] appList = new String[][]{{"1", "2", "3", "4", "5", "6", "7"},
                {"QQ", "微信", "支付宝", "百度地图", "淘宝", "京东", "天猫"}};
        int index = RandomUtils.nextInt(appList[0].length);
        return new String[]{appList[0][index], appList[1][index]};
    }

    private int getSecType(int priType) {
        int secType = 0;
        switch (priType) {
            case 1:
                int[] type1 = {1, 21, 78, 79, 80, 2, 3, 16, 22, 37, 38, 39, 40};
                secType = type1[RandomUtils.nextInt(type1.length)];
                break;
            case 2:
                int[] type2 = {4, 5, 6, 44, 7, 8, 9, 81, 82, 83, 23, 41, 42, 95, 93, 94, 26, 27, 28, 29, 30, 43, 25, 90,
                        24, 87, 88, 89, 91, 92, 32, 33, 34};
                secType = type2[RandomUtils.nextInt(type2.length)];
                break;
            case 3:
                int[] type3 = {19, 20, 46, 71, 47, 48, 49, 50, 51, 52, 72, 73, 74, 75, 76, 77, 53, 54, 55, 56, 57, 58, 59,
                        60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 84, 86};
                secType = type3[RandomUtils.nextInt(type3.length)];
                break;
            case 4:
                int[] type4 = {85};
                secType = type4[RandomUtils.nextInt(type4.length)];
                break;
            case 5:
                int[] type5 = {31};
                secType = type5[RandomUtils.nextInt(type5.length)];
                break;
            case 6:
                int[] type6 = {10, 11, 12, 13, 14, 15, 17, 18, 35, 36, 45};
                secType = type6[RandomUtils.nextInt(type6.length)];
                break;

            default:
                break;
        }
        return secType;
    }
}
