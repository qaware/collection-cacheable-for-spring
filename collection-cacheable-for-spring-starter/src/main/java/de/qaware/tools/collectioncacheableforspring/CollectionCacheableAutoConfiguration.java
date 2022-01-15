/*-
 * #%L
 * Collection Cacheable for Spring :: Starter
 * %%
 * Copyright (C) 2020 QAware GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.qaware.tools.collectioncacheableforspring;

import de.qaware.tools.collectioncacheableforspring.creator.CollectionCreator;
import de.qaware.tools.collectioncacheableforspring.creator.DefaultCollectionCreator;
import de.qaware.tools.collectioncacheableforspring.creator.SetCollectionCreator;
import de.qaware.tools.collectioncacheableforspring.returnvalue.DefaultReturnValueConverter;
import de.qaware.tools.collectioncacheableforspring.returnvalue.ListReturnValueConverter;
import de.qaware.tools.collectioncacheableforspring.returnvalue.ReturnValueConverter;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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


@Configuration(proxyBeanMethods = false)
@SuppressWarnings("java:S1118")
// suppress "Utility classes should not have public constructors" as Spring needs a public ctor
public class CollectionCacheableAutoConfiguration {

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> beanFactory.addBeanPostProcessor(new BeanPostProcessor() {

            private final CacheOperationSource cacheOperationSource = cacheOperationSource(beanFactory);

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof CacheOperationSource) {
                    return cacheOperationSource;
                } else if (bean instanceof CacheInterceptor) {
                    return collectionCacheInterceptor(beanFactory, cacheOperationSource);
                }
                return bean;
            }
        });
    }

    private static CacheInterceptor collectionCacheInterceptor(ConfigurableListableBeanFactory beanFactory, CacheOperationSource cacheOperationSource) {
        CacheInterceptor interceptor = new CollectionCacheableCacheInterceptor();
        interceptor.setBeanFactory(beanFactory);
        interceptor.configure(
                () -> beanFactory.getBeanProvider(CacheErrorHandler.class).getIfAvailable(),
                () -> beanFactory.getBeanProvider(KeyGenerator.class).getIfAvailable(),
                () -> beanFactory.getBeanProvider(CacheResolver.class).getIfAvailable(),
                () -> beanFactory.getBeanProvider(CacheManager.class).getIfAvailable()
        );
        interceptor.setCacheOperationSource(cacheOperationSource);
        return interceptor;
    }

    private static CacheOperationSource cacheOperationSource(ConfigurableListableBeanFactory beanFactory) {
        return new AnnotationCacheOperationSource(
                new SpringCacheAnnotationParser(),
                new CollectionCacheableCacheAnnotationParser(
                        beanFactory.getBeansOfType(CollectionCreator.class).values(),
                        beanFactory.getBeansOfType(ReturnValueConverter.class).values()
                )
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultCollectionCreator collectionCacheableDefaultCollectionCreator() {
        return new DefaultCollectionCreator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SetCollectionCreator collectionCacheableSetCollectionCreator() {
        return new SetCollectionCreator();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultReturnValueConverter collectionCacheableDefaultReturnValueConverter() {
        return new DefaultReturnValueConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ListReturnValueConverter collectionCacheableListReturnValueConverter() {
        return new ListReturnValueConverter();
    }
}
