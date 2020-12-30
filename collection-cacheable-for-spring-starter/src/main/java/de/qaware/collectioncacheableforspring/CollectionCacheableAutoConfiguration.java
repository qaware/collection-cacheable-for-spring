package de.qaware.collectioncacheableforspring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CollectionCacheableAutoConfiguration {
    private static final CacheOperationSource CACHE_OPERATION_SOURCE = new AnnotationCacheOperationSource(
            new SpringCacheAnnotationParser(),
            new CollectionCacheableCacheAnnotationParser()
    );

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof CacheOperationSource) {
                    return CACHE_OPERATION_SOURCE;
                } else if (bean instanceof CacheInterceptor) {
                    return collectionCacheInterceptor(beanFactory);
                }
                return bean;
            }
        });
    }

    private static CacheInterceptor collectionCacheInterceptor(ConfigurableListableBeanFactory beanFactory) {
        CacheInterceptor interceptor = new CollectionCacheableCacheInterceptor();
        interceptor.configure(
                () -> beanFactory.getBeanProvider(CacheErrorHandler.class).getIfAvailable(),
                () -> beanFactory.getBeanProvider(KeyGenerator.class).getIfAvailable(),
                () -> beanFactory.getBeanProvider(CacheResolver.class).getIfAvailable(),
                () -> beanFactory.getBeanProvider(CacheManager.class).getIfAvailable()
        );
        interceptor.setCacheOperationSource(CACHE_OPERATION_SOURCE);
        return interceptor;
    }
}
