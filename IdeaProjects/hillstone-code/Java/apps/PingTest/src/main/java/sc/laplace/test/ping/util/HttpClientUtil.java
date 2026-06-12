package sc.laplace.test.ping.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 使用示例 POST GET
 * <p>
 * public static void main(String[] args) throws IOException, URISyntaxException {
 * HttpPost httpPost = new HttpPost(new URIBuilder("https://ti.hillstonenet.com.cn/api/c2/domain/reputation").build());
 * httpPost.setHeader("X-Auth-Token", "37c647396c30fba38dc8a99a94386777ac08c572bfd78ce8fd921cb33ace7d20");
 * httpPost.setHeader("Device-SN", "0010080184514806");
 * httpPost.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
 * httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
 * httpPost.setEntity(new StringEntity(JSON.toJSONString(Collections.singletonList("baidu.com")), StandardCharsets.UTF_8));
 * ApiResponseTemplate<List<ReputationModel>> result = HttpClientUtil.execute(httpPost, new TypeReference<ApiResponseTemplate<List<ReputationModel>>>() {
 * });
 * log.info("httpPost: {}, result: {}", httpPost, result);
 * log.info("------------------------------------------------------------------------------------------");
 * HttpGet httpGet = new HttpGet(new URIBuilder("https://ti.hillstonenet.com.cn/api/permission/quota").build());
 * httpGet.setHeader("X-Auth-Token", "37c647396c30fba38dc8a99a94386777ac08c572bfd78ce8fd921cb33ace7d20");
 * Map<String, Object> result2 = HttpClientUtil.execute(httpGet, new TypeReference<Map<String, Object>>() {
 * });
 * log.info("httpGet: {}, result: {}", httpGet, result2);
 * }
 * <p>
 * 2024/01/31 10:00:08
 *
 * @author jxwu
 */
@Slf4j
@UtilityClass
public class HttpClientUtil {
    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    /**
     * 看上述使用示例
     */
    public static <T, R extends HttpUriRequest> T execute(R request, TypeReference<T> typeReference) throws IOException {
        try (CloseableHttpClient httpClient = createDefault()) {
            return httpClient.execute(request, response -> {
                final StatusLine statusLine = response.getStatusLine();
                final HttpEntity entity = response.getEntity();
                if (statusLine.getStatusCode() >= 300) {
                    EntityUtils.consume(entity);
                    throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                }
                return entity == null ? null : JSON_MAPPER.readValue(EntityUtils.toString(entity, StandardCharsets.UTF_8), typeReference);
            });
        }
    }

    public static CloseableHttpClient createDefault() {
        try {
            // 创建跳过证书验证的CloseableHttpClient
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // 上述创建失败则创建默认客户端
        return HttpClients.createDefault();
    }
}
