package common.config;

import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("*.*.*.impl")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class SpringConfig {
	// spring mail 設定
	@Value("${spring.mail.host}")
	private String mailHost;
	@Value("${spring.mail.port}")
	private int mailPort;
	@Value("${spring.mail.username}")
	private String mailUsername;
	@Value("${spring.mail.password}")
	private String mailPassword;

	// 在 Spring 組態中2次託管 JNDI 內的 DataSource 物件
	@Bean
	public DataSource dataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setResourceRef(true);
		bean.setJndiName("jdbc/tickeasy");
		bean.afterPropertiesSet();
		return (DataSource) bean.getObject();
	}

	// 託管 SessionFactory 物件
	@Bean
	public SessionFactory sessionFactory() throws HibernateException, IllegalArgumentException, NamingException {
		return new LocalSessionFactoryBuilder(dataSource()).scanPackages("*.vo", "*.*.vo")
				.addProperties(getHibernateProperties()).buildSessionFactory();
	}

	// 設定 hibernate 組態
	private Properties getHibernateProperties() {
		Properties props = new Properties();
		props.setProperty("hibernate.dialect", MySQLDialect.class.getName());
		props.setProperty("hibernate.show_sql", "true");
		props.setProperty("hibernate.format_sql", "true");
		props.setProperty("hibernate.current_session_context_class", SpringSessionContext.class.getName());
		return props;
	}

	// 託管 TransactionManager 物件
	@Bean
	public TransactionManager transactionManager()
			throws HibernateException, IllegalArgumentException, NamingException {
		return new HibernateTransactionManager(sessionFactory());
	}

	// 託管 JavaMailSender 物件
	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailHost);
		mailSender.setPort(mailPort);
		mailSender.setUsername(mailUsername);
		mailSender.setPassword(mailPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");

		// 根據端口決定使用 SSL 還是 STARTTLS
		if (mailPort == 465) {
			// SSL 配置
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		} else {
			// STARTTLS 配置
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
		}

		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.timeout", "10000");
		props.put("mail.smtp.writetimeout", "10000");

		return mailSender;
	}
}
