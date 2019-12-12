package cache

import org.sunbird.cache.mgr.ICacheManager
import org.sunbird.cache.util.RedisCache

object ContentCache extends ICacheManager[String]{
	/**
	 * This method provides key generation implementation for cache.
	 *
	 * @param params
	 * @return T
	 */
	override def getKey(params: String*): String = {
		"key"
	}

	/**
	 * This method provides implementation of read operation for given key
	 *
	 * @param key
	 * @param handler
	 * @return T
	 */
	override def getObject(key: String, handler: (String, String) => String = test): String = {
		println("ContentCache :::::  getObject")
		var data = RedisCache.getString(key)
		if(null==data) {
			data = handler(key, getObjectKey(key))
		}
		println("data : "+data)
		data
	}

	/**
	 * This method provides implementation of write/save operation for given key
	 *
	 * @param key
	 * @param data
	 */
	override def setObject(key: String, data: String): Unit = {

	}

	/**
	 * This method provides implementation for read operation with List Value
	 *
	 * @param key
	 * @param handler
	 * @return List[String]
	 */
	override def getList(key: String, handler: (String, String) => List[String]): List[String] = {
		var data = RedisCache.getList(key)
		if(null==data || data.isEmpty) {
			data = handler(key, getObjectKey(key))
		}
		println("data : "+data)
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

	def getObjectKey(key: String): String = {
		key
	}

	def test(key: String, objKey:String) : String = {
		"test data .........."
	}
}
