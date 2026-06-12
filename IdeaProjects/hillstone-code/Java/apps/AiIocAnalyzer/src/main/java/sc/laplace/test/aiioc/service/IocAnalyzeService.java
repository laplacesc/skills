package sc.laplace.test.aiioc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sc.laplace.test.aiioc.config.AiProperties;
import sc.laplace.test.aiioc.model.TextMessageContent;
import sc.laplace.test.aiioc.model.TextMessageEnd;
import sc.laplace.test.aiioc.model.TextMessageError;
import sc.laplace.test.aiioc.model.TextMessageStart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class IocAnalyzeService {

    private final RestTemplate aiRestTemplate;
    private final AiProperties aiProperties;
    private final ExecutorService aiStreamExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TiProxyService tiProxyService;

    @Value("classpath:prompts/ioc-system-report.md")
    private Resource systemReportPromptResource;

    @Value("classpath:prompts/ioc-user-report.md")
    private Resource userReportPromptResource;

    @Value("classpath:prompts/ioc-system-clean.md")
    private Resource systemCleanPromptResource;

    @Value("classpath:prompts/ioc-user-clean.md")
    private Resource userCleanPromptResource;

    private String systemReportPrompt;
    private String userReportPromptTemplate;
    private String systemCleanPrompt;
    private String userCleanPromptTemplate;

    @PostConstruct
    public void loadPrompts() throws IOException {
        this.systemReportPrompt = StreamUtils.copyToString(
                systemReportPromptResource.getInputStream(), StandardCharsets.UTF_8).trim();
        this.userReportPromptTemplate = StreamUtils.copyToString(
                userReportPromptResource.getInputStream(), StandardCharsets.UTF_8);
        this.systemCleanPrompt = StreamUtils.copyToString(
                systemCleanPromptResource.getInputStream(), StandardCharsets.UTF_8).trim();
        this.userCleanPromptTemplate = StreamUtils.copyToString(
                userCleanPromptResource.getInputStream(), StandardCharsets.UTF_8);
    }

    // ========== Event sending helpers ==========

    private void sendStartEvent(SseEmitter emitter, String messageId, String iocValue) throws IOException {
        Map<String, String> metadata = iocValue != null
                ? Collections.singletonMap("iocValue", iocValue) : null;
        TextMessageStart event = new TextMessageStart(messageId, metadata);
        emitter.send(SseEmitter.event().name("text")
                .data(objectMapper.writeValueAsString(event)));
    }

    private void sendContentEvent(SseEmitter emitter, String messageId, String delta) throws IOException {
        TextMessageContent event = new TextMessageContent(messageId, delta);
        emitter.send(SseEmitter.event().name("text")
                .data(objectMapper.writeValueAsString(event)));
    }

    private void sendEndEvent(SseEmitter emitter, String messageId, String finishReason) throws IOException {
        TextMessageEnd event = new TextMessageEnd(messageId, finishReason);
        emitter.send(SseEmitter.event().name("text")
                .data(objectMapper.writeValueAsString(event)));
    }

    private void sendErrorEvent(SseEmitter emitter, String messageId, String error) {
        try {
            TextMessageError event = new TextMessageError(messageId, error);
            emitter.send(SseEmitter.event().name("error")
                    .data(objectMapper.writeValueAsString(event)));
        } catch (IOException ignored) {
        }
    }

    // ========== Public API ==========

    public SseEmitter analyzeStream(String iocValue) {
        SseEmitter emitter = new SseEmitter(0L);
        String messageId = "msg_" + System.currentTimeMillis();
        aiStreamExecutor.execute(() -> {
            try {
                sendStartEvent(emitter, messageId, iocValue);

                // 查询威胁情报详情
                String reportText;
                try {
                    ResponseEntity<String> upstream = tiProxyService.queryIocDetail(iocValue);
                    reportText = upstream.getBody() == null ? "" : upstream.getBody();
                    log.info("TiProxy: {}", reportText);
                } catch (RestClientResponseException e) {
                    throw new RuntimeException("上游 " + e.getRawStatusCode() + ": " + e.getResponseBodyAsString());
                }

                String rawReport = generateReport(reportText, iocValue);
                streamClean(emitter, messageId, rawReport);
                sendEndEvent(emitter, messageId, "stop");
                emitter.complete();
            } catch (Exception e) {
                sendErrorEvent(emitter, messageId, e.getMessage());
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    // ========== Stage 1: Report generation (non-streaming) ==========

    private String generateReport(String reportText, String iocValue) {
        Map<String, Object> requestBody = buildReportRequest(reportText, iocValue);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getApiKey());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        Map response = aiRestTemplate.postForObject("/chat/completions", entity, Map.class);

        return extractContent(response);
    }

    private Map<String, Object> buildReportRequest(String reportText, String iocValue) {
        String ioc = StringUtils.hasText(iocValue) ? iocValue : "未知";
        String systemPrompt = systemReportPrompt.replace("{iocValue}", ioc);
        String userPrompt = userReportPromptTemplate
                .replace("{iocValue}", ioc)
                .replace("{reportText}", reportText == null ? "" : reportText);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiProperties.getModel());
        requestBody.put("temperature", 0.2);
        requestBody.put("messages", Arrays.asList(
                buildMessage("system", systemPrompt),
                buildMessage("user", userPrompt)
        ));
        return requestBody;
    }

    private Map<String, String> buildMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    /**
     * 移除 <think>...</think> 标签及其内部思考内容。
     */
    private String stripThinkContent(String text) {
        if (text == null) return null;
        return text.replaceAll("(?s)<think>.*?</think>\\s*", "").trim();
    }

    private String extractContent(Map response) {
        if (response == null) {
            return "AI response is empty.";
        }
        Object choicesObject = response.get("choices");
        if (!(choicesObject instanceof List) || ((List) choicesObject).isEmpty()) {
            return "AI response choices are empty.";
        }
        Object firstChoice = ((List) choicesObject).get(0);
        if (!(firstChoice instanceof Map)) {
            return "AI response choice format is invalid.";
        }
        Object messageObject = ((Map) firstChoice).get("message");
        if (!(messageObject instanceof Map)) {
            return "AI response message format is invalid.";
        }
        Object content = ((Map) messageObject).get("content");
        return stripThinkContent(content == null ? "AI response content is empty." : content.toString());
    }

    // ========== Stage 2: Streamed cleaning ==========

    private void streamClean(SseEmitter emitter, String messageId, String rawReport) {
        HttpURLConnection conn = null;
        try {
            String userPrompt = userCleanPromptTemplate
                    .replace("{rawReport}", rawReport == null ? "" : rawReport);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiProperties.getModel());
            requestBody.put("temperature", 0.1);
            requestBody.put("stream", Boolean.TRUE);
            requestBody.put("messages", Arrays.asList(
                    buildMessage("system", systemCleanPrompt),
                    buildMessage("user", userPrompt)
            ));

            byte[] payload = objectMapper.writeValueAsBytes(requestBody);

            URL url = new URL(aiProperties.getBaseUrl().replaceAll("/+$", "") + "/chat/completions");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(120_000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setRequestProperty("Authorization", "Bearer " + aiProperties.getApiKey());
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload);
            }

            int status = conn.getResponseCode();
            if (status / 100 != 2) {
                String err = readAll(conn.getErrorStream());
                throw new RuntimeException("upstream " + status + ": " + err);
            }

            readSseStream(conn, emitter, messageId);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void readSseStream(HttpURLConnection conn, SseEmitter emitter, String messageId) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            boolean inThink = false;
            boolean stripLeadingNewlines = true;
            String leftover = "";
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data:")) {
                    continue;
                }
                String data = line.substring(5).trim();
                if (data.isEmpty()) {
                    continue;
                }
                if ("[DONE]".equals(data)) {
                    break;
                }
                String delta = extractDelta(data);
                if (delta == null || delta.isEmpty()) {
                    continue;
                }
                // 安全网：过滤 <think>...</think> 内容（即使 reasoning_split 生效，也防范 API 异常）
                String text = leftover + delta;
                leftover = "";

                StringBuilder clean = new StringBuilder();
                int pos = 0;
                while (pos < text.length()) {
                    if (!inThink) {
                        int start = text.indexOf("<think>", pos);
                        String segment = start < 0 ? text.substring(pos) : text.substring(pos, start);
                        // 跳过前导换行符（流开头 或 刚退出 <think> 块时）
                        if (stripLeadingNewlines) {
                            int skip = 0;
                            while (skip < segment.length() && (segment.charAt(skip) == '\n' || segment.charAt(skip) == '\r')) {
                                skip++;
                            }
                            if (skip > 0) {
                                segment = segment.substring(skip);
                            }
                            // 只在真正产生内容时消耗标志，纯换行 chunk 保留给下一段
                            if (!segment.isEmpty()) {
                                stripLeadingNewlines = false;
                            }
                        }
                        if (!segment.isEmpty()) {
                            clean.append(segment);
                        }
                        if (start < 0) {
                            break;
                        }
                        inThink = true;
                        pos = start + 7; // skip "<think>"
                    } else {
                        int end = text.indexOf("</think>", pos);
                        if (end < 0) {
                            // 未闭合 — 可能是跨 chunk，保留到下一次
                            leftover = text.substring(pos);
                            break;
                        }
                        inThink = false;
                        stripLeadingNewlines = true;
                        pos = end + 8; // skip "</think>"
                    }
                }
                if (clean.length() > 0) {
                    sendContentEvent(emitter, messageId, clean.toString());
                }
            }
        }
    }

    private String extractDelta(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode content = choices.get(0).path("delta").path("content");
                if (content.isTextual()) {
                    return content.asText();
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    private String readAll(java.io.InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
