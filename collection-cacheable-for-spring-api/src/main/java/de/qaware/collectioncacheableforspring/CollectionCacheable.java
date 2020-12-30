/*-
 * #%L
 * Collection Cacheable for Spring :: API
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

package de.qaware.collectioncacheableforspring;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation for filling the cache from methods returning a {@link
 * java.util.Map} and accepting a {@link java.util.Collection} of IDs.
 *
 * <p>This annotation is related to {@link org.springframework.cache.annotation.Cacheable}.
 * See the project's README for a detailed explanation how this annotation should be used.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CollectionCacheable {

    /**
     * Alias for {@link #cacheNames}.
     *
     * @see #cacheNames
     */
    @AliasFor("cacheNames")
    String[] value() default {};

    /**
     * @see #value
     * @see org.springframework.cache.annotation.Cacheable#cacheNames
     */
    @AliasFor("value")
    String[] cacheNames() default {};

    /**
     * The {@code p0} parameter points to an item of the
     * given collection, not to the collection itself.
     *
     * @see org.springframework.cache.annotation.Cacheable#key
     */
    String key() default "";

    /**
     * @see org.springframework.cache.annotation.Cacheable#keyGenerator
     */
    String keyGenerator() default "";

    /**
     * @see org.springframework.cache.annotation.Cacheable#cacheManager
     */
    String cacheManager() default "";

    /**
     * @see org.springframework.cache.annotation.Cacheable#cacheResolver
     */
    String cacheResolver() default "";

    /**
     * @see org.springframework.cache.annotation.Cacheable#condition
     */
    String condition() default "";

    /**
     * @see org.springframework.cache.annotation.Cacheable#unless
     */
    String unless() default "";

    /**
     * If set to true, items which are not found in the result
     * map will be put as {@code null} into the cache. A
     * subsequent invocation won't query those items again.
     */
    boolean putNull() default false;
}
