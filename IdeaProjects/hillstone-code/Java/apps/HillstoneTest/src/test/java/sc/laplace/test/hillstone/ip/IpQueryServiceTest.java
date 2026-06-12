package sc.laplace.test.hillstone.ip;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
class IpQueryServiceTest {

    @Resource
    IpQueryService ipQueryService;

    @Test
    void queryIp138() throws DocumentException {
        log.info("{}", ipQueryService.queryIp138("222.92.61.109"));
        log.info("{}", ipQueryService.queryIp138("119.129.73.231"));
        log.info("{}", ipQueryService.queryIp138("188.253.121.148"));
    }

    @Test
    void queryCip() {
        log.info("{}", ipQueryService.queryCip("222.92.61.109"));
        log.info("{}", ipQueryService.queryCip("119.129.73.231"));
        log.info("{}", ipQueryService.queryCip("188.253.121.148"));
    }

    @Test
    void queryPconline() throws JsonProcessingException {
        log.info("{}", ipQueryService.queryPconline("222.92.61.109"));
        log.info("{}", ipQueryService.queryPconline("119.129.73.231"));
        log.info("{}", ipQueryService.queryPconline("188.253.121.148"));
    }

    @Test
    void queryChinaz() throws JsonProcessingException {
        log.info("{}", ipQueryService.queryChinaz("222.92.61.109"));
        log.info("{}", ipQueryService.queryChinaz("119.129.73.231"));
        log.info("{}", ipQueryService.queryChinaz("188.253.121.148"));
    }

    @Test
    void getRegion() throws JsonProcessingException {
        log.info("{}", ipQueryService.getRegion("222.92.61.109"));
        log.info("{}", ipQueryService.getRegion("119.129.73.231"));
        log.info("{}", ipQueryService.getRegion("188.253.121.148"));
    }
}