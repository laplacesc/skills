package sc.laplace.test.hillstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author jxwu
 */
@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
public class HillstoneTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HillstoneTestApplication.class, args);
    }

}
