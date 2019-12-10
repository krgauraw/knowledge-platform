package org.sunbird.cache.impl;

import org.sunbird.cache.handler.ContentCacheHandler;
import org.sunbird.cache.mgr.RedisCacheManager;

import java.util.List;

public class ContentCacheImpl extends RedisCacheManager {

	public ContentCacheImpl() {
		handler = new ContentCacheHandler();
	}
	@Override
	public String getKey(String... params) {
		return String.join("_", params);
	}

	@Override
	public String getString(String key) {
		return getStringData(key, key);
	}

	@Override
	public void setString(String key, String data, int ttl) {

	}

	@Override
	public List<String> getList(String key) {
		return null;
	}

	@Override
	public void setList(String key, List<String> list, int ttl) {

	}

	@Override
	public void increment(String key) {

	}

	@Override
	public void delete(String... key) {

	}
}
