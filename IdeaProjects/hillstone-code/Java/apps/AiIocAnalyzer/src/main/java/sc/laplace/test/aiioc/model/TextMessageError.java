package sc.laplace.test.aiioc.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TextMessageError {
    private String type = "error";
    private String messageId;
    private String error;

    public TextMessageError(String messageId, String error) {
        this.messageId = messageId;
        this.error = error;
    }
}
