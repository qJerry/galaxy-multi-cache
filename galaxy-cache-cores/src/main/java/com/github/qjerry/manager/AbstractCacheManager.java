package com.github.qjerry.manager;

import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.layer.Cache;
import com.github.qjerry.listener.RedisMessageListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Slf4j
@Getter
@Setter
public abstract class AbstractCacheManager implements CacheManager, InitializingBean, DisposableBean, BeanNameAware, SmartLifecycle {

	private RedisMessageListenerContainer container = new RedisMessageListenerContainer();

	private final RedisMessageListener messageListener = new RedisMessageListener();

	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * key：cache name
	 * value-key：key
	 */
	private final ConcurrentMap<String, ConcurrentMap<String, Cache>> cacheContainer = new ConcurrentHashMap<>(16);

	private volatile Set<String> cacheNames = new LinkedHashSet<>();

	static Set<AbstractCacheManager> cacheManagers = new LinkedHashSet<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		this.messageListener.setCacheManager(this);
		this.container.setConnectionFactory(getRedisTemplate().getConnectionFactory());
		this.container.afterPropertiesSet();
		this.messageListener.afterPropertiesSet();
	}

	@Override
	public void destroy() throws Exception {
		container.destroy();
	}

	@Override
	public Collection<Cache> getCache(String name) {
		ConcurrentMap<String, Cache> cacheMap = this.cacheContainer.get(name);
		if (CollectionUtils.isEmpty(cacheMap))
			return Collections.emptyList();
		return cacheMap.values();
	}

	@Override
	public Cache getCache(String name, CacheMultiConfig multiConfig) {
		ConcurrentMap<String, Cache> cacheMap = this.cacheContainer.get(name);
		if (!CollectionUtils.isEmpty(cacheMap)) {
			if (cacheMap.size() > 1)
				log.warn("cache_name={} find multi config cache, pls check!", name);
			Cache cache = cacheMap.get(multiConfig.getInternalKey());
			if (cache != null)
				return cache;
		}
		synchronized (this.cacheContainer) {
			cacheMap = this.cacheContainer.get(name);
			if (!CollectionUtils.isEmpty(cacheMap)) {
				Cache cache1 = cacheMap.get(multiConfig.getInternalKey());
				if (cache1 != null)
					return cache1;
			} else {
				cacheMap = new ConcurrentHashMap<>(16);
				this.cacheContainer.put(name, cacheMap);
				this.cacheNames.add(name);
				addMessageListener(name);
			}
			Cache cache = getMissingCache(name, multiConfig);
			if (cache != null) {
				cacheMap.put(multiConfig.getInternalKey(), cache);
				if (cacheMap.size() > 1)
					log.warn("cache_name={} find multi config cache, pls check!", name);
			}
			return cache;
		}
	}

	@Override
	public Collection<String> getCacheNames() {
		return this.cacheNames;
	}

	protected abstract Cache getMissingCache(String name, CacheMultiConfig cacheConfig);

	protected void addMessageListener(String name) {
		this.container.addMessageListener(this.messageListener, ChannelTopic.of(name));
	}

	@Override
	public void start() {
		container.start();
	}

	@Override
	public void stop() {
		container.stop();
	}

	@Override
	public boolean isAutoStartup() {
		return container.isAutoStartup();
	}

	@Override
	public void stop(Runnable callback) {
		container.stop(callback);
	}

	@Override
	public boolean isRunning() {
		return container.isRunning();
	}

	@Override
	public void setBeanName(String name) {
		container.setBeanName("redisMessageListenerContainer");
	}

	@Override
	public int getPhase() {
		return container.getPhase();
	}
}
