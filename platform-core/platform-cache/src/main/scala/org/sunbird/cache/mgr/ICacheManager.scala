package org.sunbird.cache.mgr

trait ICacheManager {

	/**
	 * This method provides key generation implementation for cache.
	 *
	 * @param params
	 * @return String
	 */
	def getKey(params: String*): String

	/**
	 * This method provides implementation of read operation for given key
	 *
	 * @param key
	 * @param handler
	 * @return T
	 */
	def getObject[T: Class](key: String, handler: (String, String) => T): T

	/**
	 * This method provides implementation of write/save operation for given key
	 *
	 * @param key
	 * @param data
	 */
	def setObject[T: Class](key: String, data: T): Unit

	/**
	 * This method provides implementation for read operation with List Value
	 *
	 * @param key
	 * @param handler
	 * @return T
	 */
	def getList[T: Class](key: String, handler: (String, String) => T): T

	/**
	 * This method provides implementation for write/save operation with List Value
	 *
	 * @param key
	 * @param data
	 * @param isPartialUpdate
	 */
	def setList[T: Class](key: String, data: T, isPartialUpdate: Boolean): Unit

}
