package common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

@Configuration
// 驅動 Spring-MVC 功能
@EnableWebMvc
// 掃描 Controller 元件
@ComponentScan("*.*.*.controller")
public class SpringMvcConfig implements WebMvcConfigurer {

    // 託管 ViewResolver 物件
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setContentType("text/html;charset=UTF-8");
        registry.viewResolver(viewResolver);
    }

    // 靜態資源管理("/" 重新指向 "/")
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/");
    }

    // 託管 MappingJackson2HttpMessageConverter 物件
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        var messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setPrettyPrint(true);
        converters.add(messageConverter);
    }
}
