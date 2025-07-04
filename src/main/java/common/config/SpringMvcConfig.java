package common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
// 驅動 Spring-MVC 功能
@EnableWebMvc
// 掃描 Controller 元件
@ComponentScan("*.*.controller")
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

	// 控制預設首頁 （受到靜態資源管理的 "/" 影響）
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index.html");
	}

	// 託管 MappingJackson2HttpMessageConverter 物件
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// 設定日期格式
		var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		var objMapper = new ObjectMapper();
		objMapper.setDateFormat(sdf);
		objMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
		// 設定 messageConverter 物件
		var messageConverter = new MappingJackson2HttpMessageConverter(objMapper);
		messageConverter.setPrettyPrint(true);
		converters.add(messageConverter);
	}
}
