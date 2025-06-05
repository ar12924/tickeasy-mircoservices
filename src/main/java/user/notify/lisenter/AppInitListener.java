package user.notify.lisenter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppInitListener implements ServletContextListener {

	
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Web 應用啟動了！");
        
        // 啟動排程或資源初始化
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Web 應用關閉了！");
        // 資源清理
    }
}
