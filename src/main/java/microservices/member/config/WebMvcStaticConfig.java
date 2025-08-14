package microservices.member.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcStaticConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 讓 Spring Boot 直接服務 src/main/webapp 內的靜態檔案
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/", "file:src/main/webapp/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // 允許直接以視圖名稱解析 JSP，若你之後要加 @Controller return "login" 可用
        registry.jsp("/", ".jsp");
    }
}


