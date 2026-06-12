package sc.laplace.test.superset.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 全局配置 —— 允许前端跨域访问。
 * <p>
 * 如果前端与后端同源部署（都通过 Spring Boot 的 static 目录提供服务），本配置不是必须的。
 * 保留以支持独立部署的前端（如 Vite dev server、React 等）。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
