package cache

import org.sunbird.cache.mgr.ICacheManager
import org.sunbird.cache.util.RedisCache

object ContentCache extends ICacheManager[String] {

	/**
	 * This method provides key generation implementation for cache.
	 *
	 * @param params
	 * @return String
	 */
	override def getKey(params: String*): String = {
		//TODO: Provide Implementation Or Throw An Exception
		"cache-key"
	}

	/**
	 * This method provides implementation of read operation for given key
	 *
	 * @param key
	 * @param handler
	 * @return String
	 */
	override def getObject(key: String, handler: (String, String) => String = contentHandler): String = {
		var data = RedisCache.getString(key)
		if (null == data)
			data = handler(key, key)
		data
	}

	/**
	 * This method provides implementation of write/save operation for given key
	 *
	 * @param key
	 * @param data
	 * @param ttl
	 */
	override def setObject(key: String, data: String, ttl: Int): Unit = {
		RedisCache.saveString(key, data, ttl)
	}

	/**
	 * This method provides implementation for read operation with List Value
	 *
	 * @param key
	 * @param handler
	 * @return List[String]
	 */
	override def getList(key: String, handler: (String, String) => List[String] = contentListHandler): List[String] = {
		var data = RedisCache.getList(key)
		if (null == data || data.isEmpty)
			data = handler(key, key)
		data
	}

	/**
	 * This method provides implementation for write/save operation with List Value
	 *
	 * @param key
	 * @param data
	 * @param isPartialUpdate
	 */
	override def setList(key: String, data: List[String], isPartialUpdate: Boolean = false): Unit = {
		RedisCache.saveList(key, data, isPartialUpdate)
	}

	private def contentHandler(cacheKey: String, objKey: String): String = {
		//TODO : Provide Implementation
		"sample data from default handler..."
	}

	private def contentListHandler(cacheKey: String, objKey: String): List[String] = {
		//TODO : Provide Implementation
		List[String]("test-habdler-val1", "test-handler-val2")
	}

	/**
	 * This method provides implementation for increment operation for value of given key
	 *
	 * @param key
	 * @return Double
	 */
	override def increment(key: String): Double = ???

	/**
	 * This method provides implementation for reset/delete operation for given key/keys
	 *
	 * @param key
	 */
	override def delete(key: String*): Unit = ???

	/**
	 *
	 * @param key
	 * @param data
	 */
	override def delete(key: String, data: List[String]): Unit = ???

	/**
	 * This method provides implementation for publish message operation to Redis Channel.
	 *
	 * @param channel
	 * @param message
	 */
	override def publish(channel: String, message: String): Unit = ???

	/**
	 * This method provides implementation for subscribe operation to Redis Channel.
	 *
	 * @param channels
	 */
	override def subscribe(channels: String*): Unit = ???
}
