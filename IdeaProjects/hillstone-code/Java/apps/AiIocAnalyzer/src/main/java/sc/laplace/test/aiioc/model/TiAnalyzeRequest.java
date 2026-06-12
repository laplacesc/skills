package sc.laplace.test.aiioc.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
public class TiAnalyzeRequest {

    private List<Object> context;

    @Valid
    @NotEmpty
    private List<Message> messages;

    private String runId;

    private String threadId;

    private List<Object> tools;

    @Data
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
        private String id;
    }
}
