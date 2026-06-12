package sc.laplace.test.hillstone;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class MainTest {

    @Test
    public void createJWT() {
        String token = JWT.create()
                .withClaim("username", "SysAdmin")
                .withExpiresAt(Date.from(Instant.ofEpochSecond(LocalDateTime.now().plusYears(1).toEpochSecond(ZoneOffset.ofHours(8)))))
                .withClaim("roleId", 2)
                .sign(Algorithm.HMAC256("datafort"));
        log.info("token: {}", token);
    }

    @Test
    public void testCsv() throws IOException {
        try (CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(Paths.get("test.csv")), CSVFormat.DEFAULT)) {
            csvPrinter.printRecord(Arrays.asList(1, 2, "123", StringEscapeUtils.escapeJava("1.1.1\n"), StringEscapeUtils.escapeJava("2\t\n3")));
        }
    }

    @Test
    public void sha256() throws IOException {
        log.info("{}", DigestUtils.sha256Hex("50.23.209.199"));
    }
}