package common.util;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CommonUtil {
    private static final Logger logger = LogManager.getLogger(CommonUtil.class);
    
    // 將 ServletContext 系列物件，放入 ioc 容器
    public static <T> T getBean(ServletContext sc, Class<T> clazz) {
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        return context.getBean(clazz);
    }
}
