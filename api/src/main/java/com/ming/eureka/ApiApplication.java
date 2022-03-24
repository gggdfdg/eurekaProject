package com.ming.eureka;

import com.google.common.collect.Sets;
import com.ming.eureka.dto.StringToTimeConvertor;
import com.ming.eureka.jpa.CustomJpaRepositoryFactoryBean;
import com.ming.eureka.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableEurekaClient
@ServletComponentScan
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "com.ming.eureka", repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory jedisConnectionFactory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        return redisTemplate;
    }

    /**
     * 自定义converter
     *
     * @return
     */
    @Bean
    public ConversionServiceFactoryBean converter() {
        ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
        factory.setConverters(Sets.newHashSet(new StringToTimeConvertor()));
        return factory;
    }

    @Bean
    public FindByIndexNameSessionRepository redisSessionRepository(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisIndexedSessionRepository(redisTemplate);
    }

    @Bean
    public SecurityConfig applicationSecurity() {
        return new SecurityConfig();
    }
}