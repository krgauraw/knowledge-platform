package org.sunbird.cache.locator;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.cache.mgr.ICacheManager;
import org.sunbird.common.exception.ServerException;

import java.util.HashMap;
import java.util.Map;

public class CacheLocator {

	private static Map<String, Object> impls = new HashMap<String, Object>();

	public void register(String key, Object instance){
		if(StringUtils.isNotBlank(key) && instance instanceof ICacheManager)
			impls.put(key, instance);
		else
			throw new ServerException("ERR_INVALID_CACHE_IMPL","Invalid Cache Implementation Received.");
	}

	public ICacheManager getImplementation(String key){
		if(MapUtils.isNotEmpty(impls) && impls.containsKey(key))
			return (ICacheManager) impls.get(key);
		else
			throw new ServerException("ERR_INVALID_CACHE_IMPL","Implementation Not Found");
	}
}
