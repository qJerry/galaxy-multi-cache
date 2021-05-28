package com.github.qjerry.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@Getter
@Setter
@AllArgsConstructor
public class CacheExpressionRootObject {

	private final Method method;
	private final Object[] args;
	private final Object target;
	private final Class<?> targetClass;
}
