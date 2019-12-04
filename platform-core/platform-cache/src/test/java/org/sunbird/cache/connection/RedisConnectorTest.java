package org.sunbird.cache.connection;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Test Cases for RedisConnector
 *
 * @see RedisConnector
 */
public class RedisConnectorTest {

	@Test
	public void testGetConnection() {
		Jedis conn = RedisConnector.getConnection();
		assertNotNull(conn);
		assertTrue(conn instanceof Jedis);
		RedisConnector.returnConnection(conn);
	}

}
