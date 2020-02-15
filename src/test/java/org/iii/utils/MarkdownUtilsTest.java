package org.iii.utils;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;

import org.iii.utils.MarkdownUtils;
import org.iii.utils.HeaderAlign;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MarkdownUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void mdTableHeaderTest() {
		
		List<String> headers = Arrays.asList("#", "Name", "Age", "Addr");
		List<HeaderAlign> aligns = Arrays.asList(HeaderAlign.Right, HeaderAlign.Left, null, null);

		String resp = MarkdownUtils.mdTableHeader(headers, aligns);

		String expected = "|#|Name|Age|Addr|\n"
						+ "|--:|:---|---|----|";
		assertEquals(expected, resp);
	}

}
