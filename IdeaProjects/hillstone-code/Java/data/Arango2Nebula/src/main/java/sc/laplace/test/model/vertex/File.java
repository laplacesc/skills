package sc.laplace.test.model.vertex;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import sc.laplace.test.util.VidHelper;

import java.io.Serializable;

/**
 * @author jxwu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class File extends Ioc implements Vertex, Serializable {
    /**
     * MD5
     */
    @JsonProperty("hash_md5")
    private String hashMd5;
    /**
     * SHA1
     */
    @JsonProperty("hash_sha1")
    private String hashSha1;
    /**
     * SHA256
     */
    @JsonProperty("hash_sha256")
    private String hashSha256;
    /**
     * SHA512
     */
    @JsonProperty("hash_sha512")
    private String hashSha512;
    /**
     * 文件类型
     */
    @JsonProperty("file_type")
    private String fileType;
    /**
     * 文件大小
     */
    @JsonProperty("file_size")
    private Long fileSize;

    @Override
    public String vid() {
        String hash = firstNonBlank(hashMd5, hashSha1, hashSha256, hashSha512);
        // file 顶点与 hash_* 顶点共用 hash 内容时，增加命名空间避免 VID 冲突。
        return VidHelper.vidFromFileHash(hash);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null) {
                String normalized = value.trim();
                if (!normalized.isEmpty()) {
                    return normalized;
                }
            }
        }
        return null;
    }
}
