package sc.laplace.test.hillstone.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EncryptUtilTest {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    /**
     * <a href="https://1password.com/zh-cn/password-generator">...</a>
     */
    @Test
    public void test() throws JsonProcessingException {
        List<ImmutableMap<String, Collection<String>>> accounts = Arrays.asList(
                generate("operations-tip", "G!d?Fsj?]u~{&gKku,J="),
                generate("operations-sandbox", "GE?(#N*y]|&%BS7rE}3S"),
                generate("operations-ngcv-aliyun", "nO~mA^H5OlVn8Rg%.<@N"),
                generate("operations-ngcv-damddos", "v2^S8XP$=8`(^VA25NV)"),
                generate("operations-update-server", "%QVGn=+d6|SOS.34$m1i"),
                generate("operations-ips", "%#Ml)bgRS3o$Kv0J0B#."),
                generate("operations-isource", ":xi;sdAjTu8&D0TZhUJ)"),
                generate("operations-cloud-armour", "&ejE;V(XxWJNM-4W~snv"),
                generate("operations-tac", "73Eoto}SQ6[NHsWEXs*J"),
                generate("operations-iot", "SNPgHECV-I5&&0><YzhU"),
                generate("operations-lingyan", "M99rdK,vL:cO,tbQygPA"),
                generate("operations-intelligence", "usi2D2+gzpEYV=^Az*dg"),
                generate("jxwu", "jxwu"),
                generate("operations-poc", "P0Zr4dpE*j:WRE%Ae}Vn"),
                generate("operations-edr", "A3+^QgGrrETPKNufamo*"),
                generate("operations-xdr", "xm9A3f1}^X!KW1dG?_X>"),
                generate("operations-test-center", "hGR5!r-tmAVXg:6L-rb3"),
                generate("operations-mssp", "Mh@?BB6uA2?PV+y_b.e#")
        );

        log.info("{}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(accounts));
    }

    public ImmutableMap<String, Collection<String>> generate(String username, String password) {
        return ImmutableMultimap.<String, String>builder()
                .put("username", username)
                .put("username", EncryptUtil.encrypt(username))
                .put("username", EncryptUtil.encrypt(username.toLowerCase()))
                .put("password", password)
                .put("password", EncryptPWDUtil.encrypt(password))
                .put("apikey", TokenUtil.generateToken64())
                .build().asMap();
    }

    @Test
    public void testDecrypt() throws JsonProcessingException {

        /*
        select userName
        from hsm_db.t_user
        where hsm_db.t_user.`userId` in
        (select userId
        from hsm_db.t_oauth_user_client_permission toucp
        where toucp.clientId = 3 and toucp.permissionId = 2);
         */

        List<String> userNameList = new ArrayList<>(Arrays.asList(
                "0nk82GfKoW9jlDpFL6NP7HPQtBMJ/OA5TsVV+1PSBps=",
                "9m8GssgNrfZnlnhgu3z5H9BeS1w9n31w4tkVAYlbpkU=",
                "B1oLu8WmScAMW/VW7k6elkm1kL8vFqQz5w+edUiUqE4=",
                "/oUr83r/IDarYW+7q1uTQMCMFnR4ilT9JhVfggEWFHA=",
                "upfZipegip8ErOqMh8cr5BIF8G/slbbrKmkbY+SCQT0=",
                "61bDtfml9i0+IR6soH2EXYscRW7NEdF6ZMj4lPnUE3A=",
                "sURruC4p3W3ihDgnefT+jTi1UYYPFrj242R/FbpwS/A=",
                "QPAMQcqkPzFXzRdwhAQXFkKiCmlOEcEaBtxjVd/Xsa4=",
                "n+77AgNr/6LBYPfgccBQQShQYr+aZbksPOH8lQiDz0A=",
                "6fWsGa+N8eNUtsCdqmr1vZvE2eBKS2jCL8bvCCA2NlU=",
                "A46gVFurKvy35ccX8aVW0FfpokbsLQejf6t61gu1BWI=",
                "b1zZGJoAif7GsdcV4ZfyRe4GklgvID7XauEFXgfzuLg=",
                "xWtBU5y2fVZA9Ho+fcPtr02fkxZt7ZtvU63PMLMhncM=",
                "c6W4dE064+5L/QT+hRUNJ6IPu2znkjI8PxwUEIikyDs=",
                "7vkFtHnvS/oj7z9MnFkHN44VfjK4dQd2+lSiAU6rRJo=",
                "5H15GY7ZC4vHFr3jDu+vFeWEVUee6wgVx72I+MDo/YU=",
                "PiiTLG+ScDzRcEEVJRLmurddjU/55NOes1jFH1bhhq4=",
                "RJEXPVFMcLgn5JNUwrhjsz9rCYyn9hB+RbankSIawKE=",
                "fzFpnSQ1OZOWP1OQE6HGZE8rq5J2iSPdu13KHPtN1m0=",
                "U0kmDpi0GzCAxEx2izU2NPN6DHIEPduLPgqdCDimCkU=",
                "tar9dAfclR2zkfdgVz78fHZCdMMuIGTCNPOEzExC8CE=",
                "mM3ccAUPhplxWZTgcfeiEzrnbupnt79/iLmOnD9nuvQ=",
                "TNoghl/Hpu3WzxNIwGDZQM9TrFXf3wW5pDkEMn6zyGM=",
                "vyInN05Nc0f/nvF2Sy55ruy1BjtDMJ1TyH5bXxKzZp4=",
                "vgkLorrBA2m2obn/Gt3WmUchAzJJCdlZKRvuodWkviA=",
                "s6datkMvBoKStwQBTNE5HRm4nph6xl9mVZCNsglcmrk=",
                "8rczCzqAGK3VYbD0C+ne06cvy+iQaSdIrM1k+vuELBA=",
                "r18SC1HJWKAJC5q7ZIta7nfp8HStd8yMluie1K6+qzQ=",
                "a5XtItNJvFVbp9GkX33OGHJhEAlBZhxV7oRAnHnNrbU=",
                "GI3Y/gGM9cP+kabViwSzlnbzMUJx0UgEVXhT+9VIqR4=",
                "ykaZGLnubLlOYSTWKSum24dMVjtXT2oZCsZdXYMOmgs=",
                "XgXSmusY26Iwp3EqKRq4w0zTU0INwI7S5isT2rEeA6I=",
                "72mQZ1AXEEQxVEJ1Y17BKQ+BwYgRiSfDSsrd9dSXlZg=",
                "muHWOOAgJwsjewHZeibuhWX6KUkjss/A78ZZGjaodco=",
                "njv5t9gJdetiP7c/Zpx6a8SjFqWLggbR3u8oAe0wXW0=",
                "FDOSk5uQsWTBN2rGYuhT50J9N5VwaYiqeN8uBM4decw=",
                "qQ4A5a+OSVlsM1pcEcM7qZ+vizVskLZu9D5EzsqniNw=",
                "sz6bSyvTG9q/QgVYBe82GgmwFuJFTdltvos3Z78C95Q=",
                "+UWhtCU9yjY1yIzE4ECjPHjb1CAp4NDYwzaVQo9aVDw=",
                "vdkN7E6Z6+yjjm/X4KttQwbVIspRBUyRU7YgSQSlsCs=",
                "tXb6YikiQnxQF3L08boEcVoGukNznWWht76v1Lqj2rg=",
                "6BqwaGZc/q+GrcQ7mZbJGqbZMEZYYRba9qMcNlCC9uU=",
                "QOaz2OV9LPwCp6r5UQBe7F2GI/nbe7iHJrLzLAhbl9E=",
                "S1zrCYzEpALEUPReDnS5PcKx3iGo5StDF/KrYarwZHk=",
                "y55oo9gg4i3Rlv/7C6MxiCR//+LrxlEO/JBRj/HNQRc=",
                "jDEy8F6f25zoQiYUBRVreN5w0SAyNy8IjChmXtCoso4=",
                "nUxcLweig8W+r96HemOZCsvxx49gTRmtfGI20MDo/ZQ=",
                "psru3SsmIbS+fQOUCNRqew7KsryOQXzgyAPdEWtOcwU=",
                "5NyRT9j2QpOP9gDG9gMdHpWnNKiFVV7kjv9BJ8IUBBM=",
                "h6mHq8QaVCLOGhFH2bvvhjL2OwlRgC3n9eEgIVHsZKE=",
                "/7H9xIbXi32y9vJuaXxeUJORPDZ9djYYiFdXTbgrXzo=",
                "9jlkFxqyOiBi3brgxfoPDZqYZ3zR0jnSqgct+vHv2Eg=",
                "MAq/LCQ5kqM0NPtHXdEcu+Va1Q8zQm60b0kSc6YTEsE=",
                "MIQX0uHQ0ueVlKLB6IERlOhu/Hexy9/NIYu6mrFgtzI=",
                "7KgFqluWIws7Txnlamuie5WmbsxWL+rlLwS522rRuWw=",
                "SB4rMRFJoY2D+iwC8WYN3WoSbWVVcTUNeya6BIkjFck=",
                "O5ZJ3Vt1xLloXZpDb+AKgQ5dnAJSQ6ZlbLTqv3OXz2Q=",
                "D6UaowdR74Y5RwUC5vdc1RZRDmLX0e4+KmLAqd3Fdok=",
                "XBGmaCsFeJyFTDguXyI9yYFgVZItvKiajL0YhvXL8qA=",
                "ySNzBXv+LsweMXZWUjVaxFgleRnIsLl2hV2S0Y90mjw=",
                "xq/09dnWEWVLx1TilQDke9eU9a7+UKfw8Y0nsmQtqc4=",
                "JYJy/pS3geERscDi0NoJAHKpXmetC/ucivBrS+0Wam4=",
                "PsoQM3HdvC5mAIS4ijHiPkaPmYh3V2TiFU3Yw79CeQs=",
                "jokmzELheqHI5g2cM19WLHfQ4WWgc++0tHY/VUfl6e4=",
                "prFROsC40dxf4OwMNFlUuP7wGuY1Y505Of7xiYSgois=",
                "9NgqCJm0iQbA6/CUQDL1y+MZPoXMIXTBTpZI+wqHjc8=",
                "73hXjdCuxSia5YUdcFIdrhC7FwOP6umKPq4xFt+Atlk=",
                "PXt/MJtTrC66FTdkH/DgUDKAhFZBRs+rSqY6IarXTUc=",
                "DidSlW4Kqf6Ha8YsPJb2zjeHsIVtJ84Kma3luYZ+sLo=",
                "WGyzz5qorGLOJMreLMrR16BOK4cxPrjd9LublSt9KgU=",
                "V4lRobFqqEdl20rFkiLqCmajGhRHEXR39raGTVwpPrg=",
                "SLHG/63wT9YrZ98s354Dfbx9TJi2zls05xcafHn7fUY=",
                "KUa5eXDL8GIADadF1DAWey3kmFZXv/SieccPsxtKxNc=",
                "WJ3UUR56bJzuSkxZN4Fr9zA1Pdmx6ETHws/1FYAs7ns=",
                "rSj8ezhHqDqVU8HV0X1pUKi+E0+kcT7Hlu4HpwQhid4=",
                "vRoIGwVHHH+7BRuDprIgOq1eInQIT26muIwp3zsWzJg=",
                "dxnp9xon/Q1Po5exvdEIWUScvA4FIBRuSZbaaRIrd7Q=",
                "NfWEcxr1Yp4YK0cOluj1P6whM4SqeM7C+kMxuBRQvRY="
        ));
        log.info("{}", userNameList.size());
        log.info("{}", JSON_MAPPER.writeValueAsString(userNameList.stream().map(EncryptUtil::decrypt).collect(Collectors.toList())));
    }
}
