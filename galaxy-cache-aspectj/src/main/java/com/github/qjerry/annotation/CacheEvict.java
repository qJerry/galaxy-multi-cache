package com.github.qjerry.annotation;

import com.github.qjerry.layer.Cache;

import java.lang.annotation.*;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: Annotation indicating that a method (or all methods on a class)
 * triggers a {@link Cache#evict(Object) cache evict} operation.</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {

	/**
	 * Names of the caches to use for the cache eviction operation.
	 * @return
	 */
	String[] cacheNames() default {};

	/**
	 * Support spring Expression Language (SpEL) expression.
	 * @return
	 */
	String key() default "";

	/**
	 * TODO
	 * @return
	 */
	String cacheManager() default "";

	/**
	 * TODO
	 * @return
	 */
	String condition() default "";

	/**
	 * Whether all the entries inside the cache(s) are removed.
	 * <p>By default, only the value under the associated key is removed.
	 * <p>Note that setting this parameter to {@code true} and specifying a
	 * {@link #key} is not allowed.
	 */
	boolean allEntries() default false;

}