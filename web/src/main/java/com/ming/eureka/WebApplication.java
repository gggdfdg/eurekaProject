package com.ming.eureka;

import com.ming.eureka.jpa.CustomJpaRepositoryFactoryBean;
import com.ming.eureka.redis.RedisTokenRepositoryImpl;
import com.ming.eureka.security.ExceptionFilter;
import com.ming.eureka.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.List;

//bean缓存管理器
@EnableCaching
//开始对异步任务的支持,会自动扫描多线程(@Async)并执行
@EnableAsync
//开启定时任务,会自动扫描Job类(@Scheduled)并执行
@EnableScheduling
@SpringBootApplication
@EnableEurekaClient
@ServletComponentScan
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "com.ming.eureka", repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    /**
     * security配置
     *
     * @return
     */
    @Bean
    public SecurityConfig applicationSecurity() {
        return new SecurityConfig();
    }

    /**
     * redis模板
     *
     * @param redisConnectionFactory 能连接工厂
     * @return {@link RedisTemplate<Object, Object>}
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

    /**
     * 界面拦截
     *
     * @return
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
                factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500"));
                factory.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/403"));
                factory.addErrorPages(new ErrorPage(java.lang.Throwable.class, "/500"));
            }
        };
    }

    /**
     * redis仓库
     *
     * @param redisTemplate redis模板
     * @return {@link RedisTokenRepositoryImpl}
     */
    @Bean("redisTokenRepository")
    public RedisTokenRepositoryImpl redisTokenRepository(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisTokenRepositoryImpl(redisTemplate);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        ExceptionFilter httpBasicFilter = new ExceptionFilter();
        registrationBean.setFilter(httpBasicFilter);
        List<String> urlPatterns = new ArrayList<String>();
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setName("sysFilter");
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.INCLUDE);
        return registrationBean;
    }
}