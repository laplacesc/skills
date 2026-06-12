package sc.laplace.test.hillstone.dsgp.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jxwu
 */
@Data
@AllArgsConstructor
public class EncryptionHead {
    private String component;
    private String vendor;
    private String version;
    private String sn;
    private String type;
    private String full;
    private String format;
}
