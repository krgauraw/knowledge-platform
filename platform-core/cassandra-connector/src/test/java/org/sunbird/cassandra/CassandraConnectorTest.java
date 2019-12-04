package org.sunbird.cassandra;

import com.datastax.driver.core.Session;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests for CassandraConnector
 *
 * @see CassandraConnector
 */
public class CassandraConnectorTest extends CassandraTestSetup {

	@Test
	public void testGetSessionWithDefaultConfigExpectValidSessionObject() {
		Session session = CassandraConnector.getSession();
		assertNotNull(session);
		assertFalse(session.isClosed());
	}

	@Test
	public void testGetSessionForLPAExpectValidSessionObject() {
		Session session = CassandraConnector.getSession("lpa");
		assertNotNull(session);
		assertFalse(session.isClosed());
	}

	@Test
	public void testGetSessionForSunbirdExpectValidSessionObject() {
		Session session = CassandraConnector.getSession("sunbird");
		assertNotNull(session);
		assertFalse(session.isClosed());
	}

}
