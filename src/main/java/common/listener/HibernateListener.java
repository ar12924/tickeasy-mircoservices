package common.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import common.util.HibernateUtil5;

@WebListener
public class HibernateListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		HibernateUtil5.getSessionFactory();
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		HibernateUtil5.shutdown();
	}
}
