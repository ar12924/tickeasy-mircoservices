package common.config;

import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("*.*.*.impl")
@EnableTransactionManagement
public class SpringConfig {

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
        return new LocalSessionFactoryBuilder(dataSource())
        		.scanPackages("*.vo", "*.*.vo")
                .addProperties(getHibernateProperties())
                .buildSessionFactory();
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
}
