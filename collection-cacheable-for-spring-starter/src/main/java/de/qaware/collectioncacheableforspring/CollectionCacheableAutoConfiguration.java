package de.qaware.collectioncacheableforspring;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CollectionCacheableAutoConfiguration extends AbstractCachingConfiguration {
    private static final CacheOperationSource CACHE_OPERATION_SOURCE = new AnnotationCacheOperationSource(
            new SpringCacheAnnotationParser(),
            new CollectionCacheableCacheAnnotationParser()
    );

    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor(ObjectFactory<CacheErrorHandler> errorHandlerObjectFactory) {
        return beanFactory -> beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof BeanFactoryCacheOperationSourceAdvisor) {
                    BeanFactoryCacheOperationSourceAdvisor advisor = (BeanFactoryCacheOperationSourceAdvisor) bean;
                    advisor.setCacheOperationSource(CACHE_OPERATION_SOURCE);
                    advisor.setAdvice(collectionCacheInterceptor(errorHandlerObjectFactory));
                }
                return bean;
            }
        });
    }

    @Bean
    public CacheInterceptor collectionCacheInterceptor(ObjectFactory<CacheErrorHandler> errorHandlerObjectFactory) {
        CacheInterceptor interceptor = new CollectionCacheableCacheInterceptor();
        interceptor.configure(errorHandlerObjectFactory::getObject, this.keyGenerator, this.cacheResolver, this.cacheManager);
        interceptor.setCacheOperationSource(CACHE_OPERATION_SOURCE);
        return interceptor;
    }
}
