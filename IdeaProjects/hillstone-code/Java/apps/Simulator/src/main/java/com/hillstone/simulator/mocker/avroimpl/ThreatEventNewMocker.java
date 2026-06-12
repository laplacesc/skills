package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.mocker.IAvroMocker;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:51
 * @description: 新版威胁事件
 */
@Slf4j
public class ThreatEventNewMocker extends IAvroMocker {

    public ThreatEventNewMocker() {
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
        return "7b9ea06492c3a9af174a0f9e9e502641.avsc";
    }

    @Override
    public String getAvroMd5() {
        return "7b9ea06492c3a9af174a0f9e9e502641";
    }


    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {

        int defenderId = 1;
        int privType = 1;

        com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder =
                com.hillstone.simulator.entity.avro.model.event.threat_event.threat
                        .newBuilder()
                        .setCommon(
                                com.hillstone.simulator.entity.avro.model.event.threat_event.common
                                        .newBuilder()
                                        .setAppName("app name")
                                        .setProtocol(1)
                                        .setPolicyId(1)
                                        .setThreatProfile("threat profile")
                                        .setPcapId(1L)
                                        .setKnowledgeId(1)
                                        .setDefenderId(defenderId)
                                        .setEventInfo(
                                                com.hillstone.simulator.entity.avro.model.event.threat_event.event_info
                                                        .newBuilder()
                                                        .setEventName("event name")
                                                        .setTacticInfo(Arrays.asList("123", "1"))
                                                        .setTechniqueInfo(Arrays.asList("123", "1"))
                                                        .setBeginTime(1L)
                                                        .setEndTime(1L)
                                                        .setPriType(1)
                                                        .setSecType(1)
                                                        .setSeverity(1)
                                                        .setConfidence(1)
                                                        .setAttacksource("1.1.1.1")
                                                        .setAttackResult(1)
                                                        .setAttackNum(1)
                                                        .setAction(1)
                                                        .setAttacker(
                                                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                                                        .newBuilder()
                                                                        .setIpAddr("1.1.1.1")
                                                                        .setIpFamily(1)
                                                                        .build()
                                                        )
                                                        .setVictim(
                                                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                                                        .newBuilder()
                                                                        .setIpAddr("1.1.1.1")
                                                                        .setIpFamily(1)
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .setSourceInfo(
                                                com.hillstone.simulator.entity.avro.model.event.threat_event.source_info
                                                        .newBuilder()
                                                        .setAddr(
                                                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                                                        .newBuilder()
                                                                        .setIpAddr("1.1.1.1")
                                                                        .setIpFamily(1)
                                                                        .build()
                                                        )
                                                        .setPort(1)
                                                        .setIpMaskLen(32)
                                                        .setNatIp(
                                                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                                                        .newBuilder()
                                                                        .setIpAddr("1.1.1.1")
                                                                        .setIpFamily(1)
                                                                        .build()
                                                        )
                                                        .setNatPort(1)
                                                        .setVsysid(1)
                                                        .setInterfaceid(1)
                                                        .setVrid(1)
                                                        .setZoneid(1)
                                                        .setVsysname("vsysname")
                                                        .setVrname("vrname")
                                                        .setInterfacename("interfacename")
                                                        .setZonename("zonename")
                                                        .setAuthUser("user")
                                                        .build()
                                        )
                                        .setDstInfo(
                                                com.hillstone.simulator.entity.avro.model.event.threat_event.dst_info
                                                        .newBuilder()
                                                        .setAddr(
                                                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                                                        .newBuilder()
                                                                        .setIpAddr("1.1.1.1")
                                                                        .setIpFamily(1)
                                                                        .build()
                                                        )
                                                        .setPort(1)
                                                        .setIpMaskLen(32)
                                                        .setNatIp(
                                                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                                                        .newBuilder()
                                                                        .setIpAddr("1.1.1.1")
                                                                        .setIpFamily(1)
                                                                        .build()
                                                        )
                                                        .setNatPort(1)
                                                        .setVsysid(1)
                                                        .setInterfaceid(1)
                                                        .setVrid(1)
                                                        .setZoneid(1)
                                                        .setVsysname("vsysname")
                                                        .setVrname("vrname")
                                                        .setInterfacename("interfacename")
                                                        .setZonename("zonename")
                                                        .setAuthUser("user")
                                                        .build()
                                        )
                                        .build()
                        );

        switch (defenderId) {
            case 1:
                // 1 engine_detect 2 protocol_abnormal 3 inject_attack 4 hidden_iframe 5 extern_link 6 web_acl 7 cc_defense 8 cc_limit 9  cc_url_limit  10 brute_force 11 weak_password 12 sus_ua 13 sensitive_file
                setIps(threatBuilder, privType);
                break;
            case 10:
                // 1：Botnet C&C IP 2、3:Botnet C&C domain 4: Botnet C&C IP/port 5: Botnet C&C url 6: DNS Tunneling Communication 7 Botnet C&C Sinkhole 8 dga_detect
                setBotnet(threatBuilder, privType);
                break;
            case 7:
                setSandbox(threatBuilder);
                break;
            case 3:
                setAv(threatBuilder);
                break;
            case 4:
                setPtf(threatBuilder);
                break;
            case 9:
                setAntispam(threatBuilder);
                break;
            case 2:
                setAd(threatBuilder);
                break;
            case 6:
                // atd
                break;
            case 13:
                // web detect
                break;
            case 8:
                // decept
                break;
            case 5:
                // abd
                break;
            default:
                break;
        }


        com.hillstone.simulator.entity.avro.model.event.threat_event.threat threat = threatBuilder.build();

        try {
            DatumWriter<com.hillstone.simulator.entity.avro.model.event.threat_event.threat> userDatumWriter = new SpecificDatumWriter<>(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.class);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
            userDatumWriter.write(threat, binaryEncoder);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // 如果生成avro文件较为麻烦，也可以直接传入一个avro文件
        return new byte[0];
    }

    private void setAd(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder) {
        threatBuilder.getCommon().setDefenderId(2);
        threatBuilder.setEnginePriv(
                com.hillstone.simulator.entity.avro.model.event.threat_event.ad
                        .newBuilder()
                        .setZone("1")
                        .setTriggerReason(1)
                        .setAlarmMessage("1")
                        .setAttackTimes(1)
                        .build()
        );
    }

    private void setAntispam(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder) {
        threatBuilder.getCommon().setDefenderId(9);
        threatBuilder.setEnginePriv(
                com.hillstone.simulator.entity.avro.model.event.threat_event.antispam
                        .newBuilder()
                        .setSender("1")
                        .setRecipient("1")
                        .setSubject("1")
                        .build()
        );
    }

    private void setPtf(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder) {
        threatBuilder.getCommon().setDefenderId(4);
        threatBuilder.setEnginePriv(
                com.hillstone.simulator.entity.avro.model.event.threat_event.ptf
                        .newBuilder()
                        .setHitCount(1)
                        .setDetectIp(
                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                        .newBuilder()
                                        .setIpFamily(1)
                                        .setIpAddr("1.1.1.1")
                                        .build()
                        )
                        .setReason("1")
                        .build()
        );
    }

    private void setAv(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder) {
        threatBuilder.getCommon().setDefenderId(3);

        threatBuilder.setEnginePriv(
                com.hillstone.simulator.entity.avro.model.event.threat_event.av
                        .newBuilder()
                        .setFileName("1")
                        .setFileType("1")
                        .setFileMd5("1")
                        .setUrlMd5("1")
                        .setUrl("1")
                        .build()
        );
    }

    private void setSandbox(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder) {
        threatBuilder.getCommon().setDefenderId(7);

        threatBuilder.setEnginePriv(
                com.hillstone.simulator.entity.avro.model.event.threat_event.sandbox
                        .newBuilder()
                        .setFileName("1")
                        .setFileType("1")
                        .setSandboxMd5("1")
                        .setUrl("1")
                        .setServer(1)
                        .build()
        );
    }

    private void setIps(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder, int type) {
        Object ipsPriv = null;

        switch (type) {
            case 1:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.engine_detect
                        .newBuilder()
                        .setBlockTime(1)
                        .setBlockType(1)
                        .build();
                break;
            case 2:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.protocol_abnormal
                        .newBuilder()
                        .setBlockTime(1)
                        .setBlockType(1)
                        .build();
                break;
            case 3:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.inject_attack
                        .newBuilder()
                        .setUri("123")
                        .setAttackData("123")
                        .setInjectionPoint("123")
                        .setSite("213")
                        .build();
                break;
            case 4:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.hidden_iframe
                        .newBuilder()
                        .setHiddenIframeHeight(1)
                        .setHiddenIframeWidth(1)
                        .build();
                break;
            case 5:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.extern_link
                        .newBuilder()
                        .setRequestUri("132")
                        .setSite("123")
                        .setExternlLinkUrl("123")
                        .build();
                break;
            case 6:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.web_acl
                        .newBuilder()
                        .setSite("123")
                        .setAcl("123")
                        .setRequestUri("132")
                        .build();
                break;
            case 7:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.cc_defense
                        .newBuilder()
                        .setWebServer("213")
                        .setRequestRate(1)
                        .setThreshold(1)
                        .build();
                break;
            case 8:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.cc_limit
                        .newBuilder()
                        .setWebServer("123")
                        .setRequestRate(1)
                        .setLimitRate(1)
                        .build();
                break;
            case 9:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.cc_url_limit
                        .newBuilder()
                        .setWebServer("123")
                        .setRequestRate(1)
                        .setLimitRate(1)
                        .setBlockType(1)
                        .setBlockTime(1)
                        .build();
                break;
            case 10:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.brute_force
                        .newBuilder()
                        .setEvent("1")
                        .setAttackTimes(1)
                        .build();
                break;
            case 11:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.weak_password
                        .newBuilder()
                        .setUsername("1")
                        .setPassword("1")
                        .build();
                break;
            case 12:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.sus_ua
                        .newBuilder()
                        .setAttackData("123")
                        .build();
                break;
            case 13:
                ipsPriv = com.hillstone.simulator.entity.avro.model.event.threat_event.sensitive_file_scan
                        .newBuilder()
                        .setSensitiveFile("123")
                        .setDicHitCount(1)
                        .setHttp404Count(1)
                        .build();
                break;
            default:
                break;
        }

        threatBuilder.getCommon().setDefenderId(1);
        threatBuilder.setEnginePriv(
                com.hillstone.simulator.entity.avro.model.event.threat_event.ips
                        .newBuilder()
                        .setCommon(
                                com.hillstone.simulator.entity.avro.model.event.threat_event.ips_common
                                        .newBuilder()
                                        .setAttackContent("123")
                                        .setAttackPos("132")
                                        .setIpsType(type)
                                        .setCVEID("1")
                                        .build()
                        )
                        .setPriv(
                                ipsPriv
                        )
                        .build()
        );
    }

    private void setBotnet(com.hillstone.simulator.entity.avro.model.event.threat_event.threat.Builder threatBuilder, int type) {
        threatBuilder.getCommon().setDefenderId(10);

        Object priv = null;
        switch (type) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
                priv = com.hillstone.simulator.entity.avro.model.event.threat_event.cc_server
                        .newBuilder()
                        .setCcServer("1.1.1.1")
                        .setCheckProtocol("DNS")
                        .build();
                break;
            case 6:
                priv = com.hillstone.simulator.entity.avro.model.event.threat_event.tunnel_detect
                        .newBuilder()
                        .setTunnelDomain("1")
                        .setTunnelFeature("1")
                        .build();
                break;
            case 8:
                priv = com.hillstone.simulator.entity.avro.model.event.threat_event.dga_detect
                        .newBuilder()
                        .setDgaDomain("1")
                        .build();
                break;
            default:
                break;
        }

        threatBuilder.setEnginePriv(
                com.hillstone.simulator.entity.avro.model.event.threat_event.botnet
                        .newBuilder()
                        .setCommon(
                                com.hillstone.simulator.entity.avro.model.event.threat_event.botnet_common
                                        .newBuilder()
                                        .setMatchType(type)
                                        .setBotnetTag("tag")
                                        .setCampaignId(1)
                                        .setInvadeIp(
                                                com.hillstone.simulator.entity.avro.model.event.threat_event.ip_record
                                                        .newBuilder()
                                                        .setIpFamily(1)
                                                        .setIpAddr("1.1.1.1")
                                                        .build()
                                        )
                                        .build()
                        )
                        .setPriv(
                                priv
                        )
                        .build()
        );
    }


    /**
     * 需要生成avro model 文件需要放入指定路径
     *
     * @return
     */
    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

}
