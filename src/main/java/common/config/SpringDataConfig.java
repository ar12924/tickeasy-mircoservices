package common.config;

import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
@EnableRedisRepositories("*.*.dao")
public class SpringDataConfig {

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
		RedisTemplate<String, Object> template = new RedisTemplate<>(); // 實例化，為必要
		template.setConnectionFactory(jedisConnectionFactory()); // 從連線物件設定連線，為必要

		// 設定序列化器
		var serializer = new GenericJackson2JsonRedisSerializer();
		template.setKeySerializer(new StringRedisSerializer()); // Key 使用 String 序列化器
		template.setValueSerializer(serializer); // Value 使用 JSON 序列化器(Jackson)
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(serializer); 

		template.afterPropertiesSet(); // 內部初始化，沒有 spring boot 時為必要
		return template;
	}
}
