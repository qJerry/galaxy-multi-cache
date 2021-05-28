package com.github.qjerry;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: open multi cache</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Import({MultilevelCacheConfig.class})
public @interface EnableMultiLevelCache {}