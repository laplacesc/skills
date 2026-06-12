package sc.laplace.test.aiioc.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TextMessageContent {
    private String type = "content";
    private String messageId;
    private String delta;

    public TextMessageContent(String messageId, String delta) {
        this.messageId = messageId;
        this.delta = delta;
    }
}
