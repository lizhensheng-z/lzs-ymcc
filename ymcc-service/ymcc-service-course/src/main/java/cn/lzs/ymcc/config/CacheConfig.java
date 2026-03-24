package cn.lzs.ymcc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import javax.annotation.Resource;

/**
 * @author 李振生
 */
@Configuration
@Slf4j
public class CacheConfig extends CachingConfigurerSupport {

    @Resource
    private RedisConnectionFactory factory;

    /**
     * 自定义生成redis-key ， 类名.方法名
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName()).append(".");
            sb.append(method.getName()).append(".");
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            System.out.println("keyGenerator=" + sb.toString());
            return sb.toString();
        };
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver(cacheManager());
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 用于捕获从Cache中进行CRUD时的异常的回调处理器。
        //TODO 如果Cachecrud出现异常，这里手动进行补偿
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                ignoreRedisException(key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                ignoreRedisException(key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                ignoreRedisException(key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                ignoreRedisException(null, exception);
            }

            private void ignoreRedisException(Object key, Exception e){
                e.printStackTrace();
                log.error("redis operate error key:{}", key, e);
            }
        };



    }
    //缓存管理器
    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() //不允许空值
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        //值使用JSON序列化
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(cacheConfiguration).build();
    }
}
