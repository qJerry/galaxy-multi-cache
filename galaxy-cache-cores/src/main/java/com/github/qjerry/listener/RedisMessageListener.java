package com.github.qjerry.listener;

import com.github.qjerry.layer.Cache;
import com.github.qjerry.layer.CustomMultiCache;
import com.github.qjerry.manager.AbstractCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.Collection;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Slf4j
public class RedisMessageListener extends MessageListenerAdapter {

	private AbstractCacheManager cacheManager;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		super.onMessage(message, pattern);
		CacheSubMessage subMessage = (CacheSubMessage)this.cacheManager.getRedisTemplate().getValueSerializer().deserialize(message.getBody());
		log.debug("receive message for channel {} {}", new String(message.getChannel()), subMessage);
		try {
			Collection<Cache> caches = this.cacheManager.getCache(subMessage.getCacheName());
			for (Cache cache : caches) {
				if (cache instanceof CustomMultiCache) {
					switch (subMessage.getMessageType()) {
						case EVICT:
							if (System.currentTimeMillis() - subMessage.getSendTime() < subMessage.getExpireTime()) {
								((CustomMultiCache) cache).getCaffeineCache().evict(subMessage.getKey());
								log.debug("delete cache, name={}, key={},", subMessage.getCacheName(), subMessage.getKey());
							}
							continue;
						case CLEAR:
							if (System.currentTimeMillis() - subMessage.getSendTime() < subMessage.getExpireTime()) {
								log.debug("[Cache-Listener] clear local cache, name={}", subMessage.getCacheName());
								((CustomMultiCache) cache).getCaffeineCache().clear();
							}
							continue;
						case PREHEAT:
							continue;
					}
					log.error("not match subscribe");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCacheManager(AbstractCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
