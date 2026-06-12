package sc.laplace.test.tencent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

/**
 * @author jxwu
 */
@EnableRetry
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class TencentQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TencentQueryApplication.class, args);
    }

}
