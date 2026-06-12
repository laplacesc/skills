package sc.laplace.test.aiioc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sc.laplace.test.aiioc.config.TiProperties;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class TiProxyService {

    private final RestTemplate restTemplate;
    private final TiProperties tiProperties;

    /**
     * 根据 IOC 值自动检测类型: ip / domain / url / file
     */
    public static String detectIocType(String value) {
        if (value == null) {
            return "ip";
        }
        // IPv4
        if (value.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            return "ip";
        }
        // URL
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return "url";
        }
        // File hash: MD5(32) / SHA1(40) / SHA256(64)
        if (value.matches("[0-9a-fA-F]{32}") || value.matches("[0-9a-fA-F]{40}") || value.matches("[0-9a-fA-F]{64}")) {
            return "file";
        }
        // Domain（包含点号，非 IP 即可视为 domain）
        if (value.contains(".")) {
            return "domain";
        }
        return "ip";
    }

    public ResponseEntity<String> queryIocDetail(String iocValue) {
        String iocType = detectIocType(iocValue);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(tiProperties.getBaseUrl().replaceAll("/+$", "")
                        + "/api/" + iocType + "/detail")
                .queryParam("key", iocValue)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Auth-Token", tiProperties.getAuthToken());
        headers.set("X-API-Version", tiProperties.getApiVersion());
        headers.set("X-API-Language", tiProperties.getApiLanguage());

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }
}
