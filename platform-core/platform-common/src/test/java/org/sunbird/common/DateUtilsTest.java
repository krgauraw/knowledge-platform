package org.sunbird.common;

import org.junit.Test;
import java.util.Date;

/**
 * Unit Test Cases for DateUtils
 * @see DateUtils
 */
public class DateUtilsTest {

	@Test
	public void testFormat(){
		String result = DateUtils.format(new Date());
		System.out.println(result);
	}
}
