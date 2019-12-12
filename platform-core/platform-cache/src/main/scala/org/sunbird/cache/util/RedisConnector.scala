package org.sunbird.cache.util

import org.sunbird.cache.common.CacheErrorCode
import org.sunbird.common.Platform
import org.sunbird.common.exception.ServerException
import org.sunbird.telemetry.logger.TelemetryManager
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object RedisConnector {

	private val HOST = if (Platform.config.hasPath("redis.host")) Platform.config.getString("redis.host")
	else "localhost"
	private val PORT = if (Platform.config.hasPath("redis.port")) Platform.config.getInt("redis.port")
	else 6379
	private val MAX_CONNECTIONS = if (Platform.config.hasPath("redis.maxConnections")) Platform.config.getInt("redis.maxConnections")
	else 128
	private val INDEX = if (Platform.config.hasPath("redis.dbIndex")) Platform.config.getInt("redis.dbIndex")
	else 0
	private val config: JedisPoolConfig = new JedisPoolConfig()
	config.setMaxTotal(MAX_CONNECTIONS)
	config.setBlockWhenExhausted(true);
	private val jedisPool: JedisPool = new JedisPool(config, HOST, PORT)


	/**
	 * This Method Returns a connection object from connection pool.
	 *
	 * @return Jedis Object
	 */
	def getConnection: Jedis = try {
		val jedis = jedisPool.getResource
		if (INDEX > 0) jedis.select(INDEX)
		jedis
	} catch {
		case e: Exception =>
			TelemetryManager.error("Exception Occurred While Returning Redis Cache Connection Object to Pool.", e)
			throw new ServerException(CacheErrorCode.ERR_CACHE_CONNECTION_ERROR, e.getMessage)
	}

	/**
	 * This Method takes a connection object and put it back to pool.
	 *
	 * @param jedis
	 */
	def returnConnection(jedis: Jedis): Unit = {
		try if (null != jedis) jedisPool.returnResource(jedis)
		catch {
			case e: Exception =>
				TelemetryManager.error("Exception Occurred While Returning Redis Cache Connection Object to Pool.", e)
				throw new ServerException(CacheErrorCode.ERR_CACHE_CONNECTION_ERROR, e.getMessage)
		}
	}
}
