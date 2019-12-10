package org.sunbird.cache.handler;

public class ContentCacheHandler implements ICacheHandler{
	@Override
	public Object execute(String operation, String cacheKey, String objectKey) {
		System.out.println("ContentCacheHandler -> execute() .... Connecting to Neo4j");
		System.out.println("operation : "+operation);
		System.out.println("cacheKey : "+cacheKey);
		System.out.println("objectKey : "+objectKey);
		return "I am from ContentCacheHandler -> execute()";
	}
}
