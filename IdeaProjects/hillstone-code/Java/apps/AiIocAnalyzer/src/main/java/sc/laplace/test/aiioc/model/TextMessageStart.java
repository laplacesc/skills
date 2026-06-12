package sc.laplace.test.aiioc.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class TextMessageStart {
    private String type = "start";
    private String messageId;
    private Map<String, String> metadata;

    public TextMessageStart(String messageId, Map<String, String> metadata) {
        this.messageId = messageId;
        this.metadata = metadata;
    }
}
