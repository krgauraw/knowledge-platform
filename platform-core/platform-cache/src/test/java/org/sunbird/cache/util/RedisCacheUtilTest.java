package org.sunbird.cache.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sunbird.common.exception.ServerException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for RedisCacheUtil
 *
 * @see RedisCacheUtil
 */
public class RedisCacheUtilTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@AfterClass
	public static void finish() {
		RedisCacheUtil.deleteByPattern("kptest*");
	}

	@Test
	public void testSaveStringWithoutTtl() {
		RedisCacheUtil.saveString("kptest-01", "kptest-value-01", 0);
		String result = RedisCacheUtil.getString("kptest-01");
		assertEquals("kptest-value-01", result);
		delay(3000);
		String resultAfter3Sec = RedisCacheUtil.getString("kptest-01");
		assertEquals(result, resultAfter3Sec);
	}

	@Test
	public void testSaveStringWithTtl() {
		RedisCacheUtil.saveString("kptest-02", "kptest-value-02", 2);
		String result = RedisCacheUtil.getString("kptest-02");
		assertEquals("kptest-value-02", result);
		delay(3000);
		String resultAfter2Sec = RedisCacheUtil.getString("kptest-02");
		assertTrue(StringUtils.isEmpty(resultAfter2Sec));
	}

	@Test
	public void testGetString() {
		RedisCacheUtil.saveString("kptest-03", "kptest-value-03", 0);
		String result = RedisCacheUtil.getString("kptest-03");
		assertEquals("kptest-value-03", result);
		String anotherResult = RedisCacheUtil.getString("kptest-key-01");
		assertNull(anotherResult);
	}

	@Test
	public void testSaveList() {
		List<String> input = new ArrayList<String>() {{
			add("kptest-value-04-01");
			add("kptest-value-04-02");
		}};
		RedisCacheUtil.saveList("kptest-04", input);
		List<String> result = RedisCacheUtil.getList("kptest-04");
		assertTrue(CollectionUtils.isEqualCollection(input, result));
	}

	@Test
	public void testGetListWithWrongType() {
		exception.expect(ServerException.class);
		exception.expectMessage("WRONGTYPE Operation against a key holding the wrong kind of value");
		RedisCacheUtil.saveString("kptest-05", "kptest-value-05", 0);
		List<String> result = RedisCacheUtil.getList("kptest-05");
	}

	@Test
	public void testDelete() {
		RedisCacheUtil.saveString("kptest-06", "kptest-value-06", 0);
		RedisCacheUtil.delete("kptest-06", "kptest-07");
		String result = RedisCacheUtil.getString("kptest-06");
		assertNull(result);
	}

	@Test
	public void testDeleteByPattern() {
		RedisCacheUtil.saveString("kptestp-01", "kptestp-value-01", 0);
		RedisCacheUtil.saveString("kptestp-02", "kptestp-value-02", 0);
		RedisCacheUtil.deleteByPattern("kptestp-*");
		String result = RedisCacheUtil.getString("kptestp-01");
		assertNull(result);
		String res = RedisCacheUtil.getString("kptestp-02");
		assertNull(res);
	}

	@Test
	public void testGetIncVal() {
		RedisCacheUtil.saveString("kptest-07", "0", 0);
		Double result = RedisCacheUtil.getIncVal("kptest-07");
		Double exp = 1.0;
		assertEquals(exp, result);
		Double res = RedisCacheUtil.getIncVal("kptest-07");
		Double exp2 = 2.0;
		assertEquals(exp2, res);
	}

	@Test
	public void testPublish() {
		RedisCacheUtil.publish("test-channel-01", "test-data");
		assertTrue(true);
	}

	@Test
	public void testSaveToList() {
		List<String> input = new ArrayList<String>() {{
			add("kptest-value-08-01");
			add("kptest-value-08-02");
		}};
		RedisCacheUtil.saveList("kptest-08", input);
		List<String> result = RedisCacheUtil.getList("kptest-08");
		assertTrue(CollectionUtils.isEqualCollection(input, result));
		RedisCacheUtil.saveToList("kptest-08", new ArrayList<String>() {{
			add("kptest-value-08-03");
		}});
		List<String> res = RedisCacheUtil.getList("kptest-08");
		assertEquals(3, res.size());
		assertTrue(res.contains("kptest-value-08-03"));
	}

	@Test
	public void testDeleteFromList() {
		List<String> input = new ArrayList<String>() {{
			add("kptest-value-09-01");
			add("kptest-value-09-02");
			add("kptest-value-09-03");
		}};
		RedisCacheUtil.saveList("kptest-09", input);
		List<String> result = RedisCacheUtil.getList("kptest-09");
		assertTrue(CollectionUtils.isEqualCollection(input, result));
		assertEquals(3, result.size());
		RedisCacheUtil.deleteFromList("kptest-09", new ArrayList<String>() {{
			add("kptest-value-09-03");
		}});
		List<String> res = RedisCacheUtil.getList("kptest-09");
		assertEquals(2, res.size());
		assertFalse(res.contains("kptest-value-09-03"));
	}

	private static void delay(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
