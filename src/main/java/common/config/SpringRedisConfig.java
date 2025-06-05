package common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;


@Configuration
@EnableRedisRepositories("*.*.dao")
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

        // 讓 Redis 儲存或讀取時，用 JSON 格式處理
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    // 託管 ObjectMapper 物件
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return objectMapper;
    }
}
