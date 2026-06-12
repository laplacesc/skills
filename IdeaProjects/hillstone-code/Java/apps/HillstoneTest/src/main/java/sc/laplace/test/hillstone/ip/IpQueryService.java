package sc.laplace.test.hillstone.ip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.junmoyu.ip2region.Region;
import com.junmoyu.ip2region.RegionSearcher;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jxwu
 */
@Slf4j
@Service
public class IpQueryService {

    @Resource
    private RestTemplate restTemplate;

    public String exchange(URI uri) {
        // 模拟浏览器头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(headers);
        log.info("url: {}", uri);
        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
    }

    public Map<String, String> queryIp138(String ip) {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create("https://www.ip138.com/iplookup.php"))
                .queryParam("ip", ip)
                .build().toUri();
        Map<String, String> result = new HashMap<>();
        String body = exchange(uri);
        if (body == null) {
            return result;
        }
        Document doc = Jsoup.parse(body);
        for (Element thCell : doc.select("td.th")) {
            String title = thCell.text().trim();
            Element valueCell = thCell.nextElementSibling();
            if (valueCell == null) {
                continue;
            }
            // 运营商
            if ("运营商".equals(title)) {
                result.put("运营商", valueCell.text().trim());
            }
            // ASN归属地（ip138 上写的是 ASN归属地）
            if ("ASN归属地".equals(title)) {
                result.put("归属地", valueCell.text().trim());
            }
        }
        return result;
    }

    public Map<String, String> queryCip(String ip) {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create("http://www.cip.cc/"))
                .path(ip)
                .build().toUri();
        Map<String, String> result = new HashMap<>();
        String body = exchange(uri);
        Document doc = Jsoup.parse(body);
        // 获取 <pre> 标签内容
        Element pre = doc.selectFirst("div.data.kq-well pre");
        if (pre == null) {
            return result;
        }
        // 按行处理
        String[] lines = pre.text().split("\n");
        for (String line : lines) {
            if (line.startsWith("地址")) {
                result.put("归属地", line.split(":", 2)[1].trim());
            } else if (line.startsWith("运营商")) {
                result.put("运营商", line.split(":", 2)[1].trim());
            } else if (line.startsWith("数据二") || line.startsWith("数据三")) {
                String[] split = line.split(":", 2)[1].split("\\|");
                if (split.length == 2 && split[0] != null && split[1] != null) {
                    result.put("归属地", split[0].trim());
                    result.put("运营商", split[1].trim());
                }
            }
        }
        return result;
    }

    public Map<String, String> queryPconline(String ip) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create("https://whois.pconline.com.cn/ipJson.jsp"))
                .queryParam("ip", ip)
                .queryParam("json", true)
                .build().toUri();
        Map<String, String> result = new HashMap<>();
        String body = exchange(uri);
        if (body == null) {
            return result;
        }
        Map<String, String> json = new JsonMapper().readValue(body, Map.class);
        String[] split = json.get("addr").trim().split(" ");
        if (split.length > 0) {
            result.put("归属地", split[0].trim());
        }
        if (split.length > 1) {
            result.put("运营商", split[1].trim());
        }
        return result;
    }

    public Map<String, String> queryChinaz(String ip) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create("https://ip.chinaz.com/"))
                .path(ip)
                .build().toUri();
        Map<String, String> result = new HashMap<>();
        String body = exchange(uri);
        if (body == null) {
            return result;
        }
        // 使用 Jsoup 解析 HTML
        Document doc = Jsoup.parse(body);
        // 提取归属地信息
        Element ipAddressElement = doc.getElementById("ipmsg-ipAddress");

        if (ipAddressElement != null) {
            result.put("归属地", ipAddressElement.text());
        }
        // 提取运营商信息
        Element operatorsElement = doc.getElementById("ipmsg-operators");
        if (operatorsElement != null) {
            result.put("运营商", operatorsElement.text());
        }
        return result;
    }

    public Map<String, String> getRegion(String ip) throws JsonProcessingException {
        Region region = RegionSearcher.getRegion(ip);
        Map<String, String> result = new HashMap<>();
        result.put("归属地", Stream.of(region.getCountry(), region.getRegion(), region.getProvince(), region.getCity())
                .filter(Objects::nonNull).collect(Collectors.joining("")));
        if (region.getIsp() != null) {
            result.put("运营商", region.getIsp());
        }
        return result;
    }
}
