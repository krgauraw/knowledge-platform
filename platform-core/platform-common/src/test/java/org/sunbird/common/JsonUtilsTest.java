package org.sunbird.common;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests For JsonUtils
 * @see JsonUtils
 */
public class JsonUtilsTest {

	@Test
	public void testSerialize() throws Exception {
		// Map
		Object input = new HashMap<String, Object>(){{
			put("teststr","test-string");
			put("testlist", new ArrayList<String>(){{add("test-list-1");add("test-list-2");}});
			put("testmap", new HashMap<String, Object>(){{put("test-map-1","test-map-1-val");}});
		}};
		String result = JsonUtils.serialize(input);
		String expected="{\"testlist\":[\"test-list-1\",\"test-list-2\"],\"teststr\":\"test-string\",\"testmap\":{\"test-map-1\":\"test-map-1-val\"}}";
		assertEquals("Invalid Object Received For Serialization", expected, result);

		// List
		input = new ArrayList<String>(){{
			add("A");
			add("B");
			add("123");
		}};
		result = JsonUtils.serialize(input);
		expected = "[\"A\",\"B\",\"123\"]";
		assertEquals("Invalid Object Received For Serialization", expected, result);

		//Array
		input = new int[]{ 1,2,3,4,5,6,7,8,9,10 };
		result = JsonUtils.serialize(input);
		expected = "[1,2,3,4,5,6,7,8,9,10]";
		assertEquals("Invalid Object Received For Serialization", expected, result);
	}

	@Test
	public void testDeserializeWithStringValue() throws Exception {
		String input = "[\"A\",\"B\",\"123\"]";
		Object expected = new ArrayList<String>() {{
			add("A");
			add("B");
			add("123");
		}};
		Object result = JsonUtils.deserialize(input, ArrayList.class);
		assertTrue(CollectionUtils.isEqualCollection((List<String>) expected, (List<String>) result));
	}
}
