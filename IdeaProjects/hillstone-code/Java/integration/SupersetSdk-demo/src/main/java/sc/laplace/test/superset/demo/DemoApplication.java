package sc.laplace.test.superset.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Superset SDK 演示应用。
 * <p>
 * 以 REST API 形式暴露 Superset 数据能力，前端通过 {@code /api/} 端点访问。
 * 内置前端页面：{@code src/main/resources/static/index.html}
 */
@SpringBootApplication
@ComponentScan(basePackages = "sc.laplace.test.superset")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
