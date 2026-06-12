package sc.laplace.test.aiioc.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TextMessageEnd {
    private String type = "end";
    private String messageId;
    private String finishReason;

    public TextMessageEnd(String messageId, String finishReason) {
        this.messageId = messageId;
        this.finishReason = finishReason;
    }
}
