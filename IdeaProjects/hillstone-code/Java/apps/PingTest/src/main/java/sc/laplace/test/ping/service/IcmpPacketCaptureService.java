package sc.laplace.test.ping.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jnetpcap.*;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Icmp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import sc.laplace.test.ping.util.CipherUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author jxwu
 */
@Slf4j
@Setter
@Service
@ConfigurationProperties(prefix = "vod.capture.limit")
public class IcmpPacketCaptureService implements CommandLineRunner {

    @Value("#{'${vod.capture.monitoredIps}'.split(',')}")
    private Set<String> monitoredIps;
    private Set<String> deviceSns;
    @Value("${vod.key.decryption}")
    private String decryptionKey;

    @Override
    public void run(String... args) {
        captureIcmpPackets();
    }

    public void captureIcmpPackets() {
        log.info("monitoredIps: {}, deviceSns: {}", monitoredIps, deviceSns);
        // Will be filled with NICs
        List<PcapIf> allDevs = new ArrayList<>();
        // For any error msgs
        StringBuilder errBuf = new StringBuilder();

        // First get a list of devices on this system
        int r = Pcap.findAllDevs(allDevs, errBuf);
        if (r == Pcap.ERROR || allDevs.isEmpty()) {
            log.error("Can't read list of devices, error is {}", errBuf);
            return;
        }

        log.info("Network devices found, all devs: {}", allDevs);

        // We know we have at least 1 device
        List<PcapIf> devices = allDevs.stream()
                .filter(dev -> dev.getAddresses().stream()
                        .map(PcapAddr::getAddr)
                        // get ipv4
                        .filter(pcapSockAddr -> pcapSockAddr.getFamily() == 2)
                        // get ip data
                        .map(PcapSockAddr::getData)
                        .map(FormatUtils::ip)
                        .anyMatch(monitoredIps::contains))
                .collect(Collectors.toList());
        log.info("pick: {}", devices);

        CompletableFuture.allOf(devices.stream()
                .map(device -> CompletableFuture.runAsync(() -> open(device, new StringBuilder())))
                .toArray(CompletableFuture[]::new)).join();
    }

    private void open(PcapIf device, StringBuilder errBuf) {
        // Capture all packets, no truncation
        int snapLen = 64 * 1024;
        // capture all packets
        int flags = Pcap.MODE_PROMISCUOUS;
        // 10 seconds in milliseconds
        int timeout = 10 * 1000;
        // Open device
        Pcap pcap = Pcap.openLive(device.getName(), snapLen, flags, timeout, errBuf);

        if (pcap == null) {
            log.error("Error while opening device for capture: {}", errBuf);
            return;
        }

        PcapBpfProgram filter = new PcapBpfProgram();
        pcap.compile(filter, "icmp", 1, 0);
        pcap.setFilter(filter);

        // Start capturing packets
        PcapPacketHandler<String> jPacketHandler = (packet, user) -> {
            log.info("packet: {}", packet);
            if (packet.hasHeader(Icmp.ID)) {
                Icmp icmp = new Icmp();
                packet.getHeader(icmp);
                log.info("icmp: {}, icmp payload length: {}", icmp, icmp.getPayloadLength());
                try {
                    log.info("icmp data: {}", new String(icmp.getPayload(), StandardCharsets.UTF_8));
                    log.info("decrypted: {}",
                            new String(CipherUtil.decrypt(decryptionKey.getBytes(StandardCharsets.UTF_8),
                                    Base64.getDecoder().decode(icmp.getPayload())), StandardCharsets.UTF_8));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        };

        pcap.loop(Pcap.LOOP_INFINITE, jPacketHandler, "jNetPcap rocks!");

        // Cleanup
        pcap.close();
    }
}
