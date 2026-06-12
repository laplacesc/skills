package sc.laplace.test.aiioc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sc.laplace.test.aiioc.model.TiAnalyzeRequest;
import sc.laplace.test.aiioc.service.IocAnalyzeService;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ti")
public class TiProxyController {

    private final IocAnalyzeService iocAnalyzeService;

    @PostMapping(value = "/analyze/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter analyzeStream(@Valid @RequestBody TiAnalyzeRequest request) {
        String iocValue = request.getMessages().stream()
                .filter(m -> "user".equals(m.getRole()))
                .reduce((first, second) -> second)
                .map(TiAnalyzeRequest.Message::getContent)
                .orElseThrow(() -> new IllegalArgumentException("No user message found"));
        return iocAnalyzeService.analyzeStream(iocValue);
    }
}
