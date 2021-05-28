package com.github.qjerry.aspect;

import com.github.qjerry.annotation.CacheEvict;
import com.github.qjerry.annotation.CachePut;
import com.github.qjerry.annotation.Cacheable;
import com.github.qjerry.aspect.executor.CacheAbleExecutor;
import com.github.qjerry.aspect.executor.CacheEvictExecutor;
import com.github.qjerry.aspect.executor.CachePutExecutor;
import com.github.qjerry.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: multi cache aspect</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Aspect
@Slf4j
public class CacheMultiAspect {

	@Autowired
	private CacheAbleExecutor cacheAbleExecutor;
	@Autowired
	private CacheEvictExecutor cacheEvictExecutor;
	@Autowired
	private CachePutExecutor cachePutExecutor;

	@Around(value = "@annotation(cacheable)")
	public Object cacheAblePointCut(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
		String[] cacheNames = cacheable.cacheNames();
		checkName(cacheNames);
		return cacheAbleExecutor.execute(joinPoint, CacheConfig.build(cacheNames, cacheable.key(), cacheable.ignoreException(), cacheable.cacheLevel()), cacheable.level1Cache(), cacheable.level2Cache());
	}

	@Around(value = "@annotation(cacheEvict)")
	public Object cacheEvictPointCut(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) throws Throwable {
		String[] cacheNames = cacheEvict.cacheNames();
		checkName(cacheNames);
		return cacheEvictExecutor.execute(joinPoint, CacheConfig.build(cacheNames, cacheEvict.key(), cacheEvict.allEntries()), null, null);
	}

	@Around(value = "@annotation(cachePut)")
	public Object cachePutPointCut(ProceedingJoinPoint joinPoint, CachePut cachePut) throws Throwable {
		String[] cacheNames = cachePut.cacheNames();
		checkName(cacheNames);
		return cachePutExecutor.execute(joinPoint, CacheConfig.build(cacheNames, cachePut.key(), cachePut.cacheLevel()), cachePut.level1Cache(), cachePut.level2Cache());
	}

	private void checkName(String[] cacheNames) {
		if(Objects.isNull(cacheNames) || cacheNames.length == 0) {
			throw new IllegalArgumentException("cache name not null or empty");
		}
	}
}
