package com.github.qjerry.expression;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
public class CacheOperationExpressionEvaluator extends CachedExpressionEvaluator {

	public static final Object NO_RESULT = new Object();
	public static final Object RESULT_UNAVAILABLE = new Object();
	public static final String RESULT_VARIABLE = "result";
	private final Map<ExpressionKey, Expression> keyCache = new ConcurrentHashMap(64);
	private final Map<ExpressionKey, Expression> conditionCache = new ConcurrentHashMap(64);
	private final Map<ExpressionKey, Expression> unlessCache = new ConcurrentHashMap(64);

	private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap(64);

	public CacheOperationExpressionEvaluator() {
	}

	public EvaluationContext createEvaluationContext(Method method, Object[] args, Object target, Class<?> targetClass, Object result) {
		CacheExpressionRootObject rootObject = new CacheExpressionRootObject(method, args, target, targetClass);
		Method targetMethod = getTargetMethod(targetClass, method);
		CacheEvaluationContext evaluationContext = new CacheEvaluationContext(rootObject, targetMethod, args, this.getParameterNameDiscoverer());
		if (result == RESULT_UNAVAILABLE) {
			evaluationContext.addUnavailableVariable("result");
		} else if (result != NO_RESULT) {
			evaluationContext.setVariable("result", result);
		}
		return evaluationContext;
	}

	@Nullable
	public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
		return this.getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
	}

	public boolean condition(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
		return Boolean.TRUE.equals(this.getExpression(this.conditionCache, methodKey, conditionExpression).getValue(evalContext, Boolean.class));
	}

	public boolean unless(String unlessExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
		return Boolean.TRUE.equals(this.getExpression(this.unlessCache, methodKey, unlessExpression).getValue(evalContext, Boolean.class));
	}

	void clear() {
		this.keyCache.clear();
		this.conditionCache.clear();
		this.unlessCache.clear();
	}

	private Method getTargetMethod(Class<?> targetClass, Method method) {
		AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
		Method targetMethod = this.targetMethodCache.get(methodKey);
		if (targetMethod == null) {
			targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
			if (targetMethod == null)
				targetMethod = method;
			this.targetMethodCache.put(methodKey, targetMethod);
		}
		return targetMethod;
	}
}
