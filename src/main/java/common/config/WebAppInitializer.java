package common.config;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

// DispatcherServlet 註冊
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	// Spring Root Context 設定
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { SpringConfig.class, SpringDataConfig.class };
	}

	// Spring MVC Config 設定
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { SpringMvcConfig.class };
	}

	// 設定 DispatcherServlet 映射路徑
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	// 設定 Hibernate / UTF-8 過濾器
	@Override
	protected Filter[] getServletFilters() {
		var hibernateFilter = new OpenSessionInViewFilter();
		var charEncodingFilter = new CharacterEncodingFilter("UTF-8");
		return new Filter[] { hibernateFilter, charEncodingFilter };
	}

	// Multipart 支援 (檔案上傳功能)
	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement(""));
	}
}
