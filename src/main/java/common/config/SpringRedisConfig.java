package common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;


@Configuration
@ComponentScan("test.service.impl")
@EnableRedisRepositories(basePackages = "test.dao")
public class SpringRedisConfig {

    // 在 Spring 組態中2次託管 JNDI 內的 JedisConnectionFactory 物件
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setResourceRef(true);
        bean.setJndiName("redis/tickeasy");
        bean.afterPropertiesSet();
        return (JedisConnectionFactory) bean.getObject();
    }

    // 託管 RedisTemplate 物件 (redis 操作的基礎，如同 hibernate 的 session)
    @Bean
    public RedisTemplate<String, Object> redisTemplate() throws NamingException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
