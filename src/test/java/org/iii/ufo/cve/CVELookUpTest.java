package org.iii.ufo.cve;

//import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.BasicConfigurator;
import org.iii.ufo.cve.CVEUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class CVELookUpTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/*
	private CVELookUp cvedb;

	@Test
	public void test() throws XPathExpressionException, XPathFactoryConfigurationException, ParserConfigurationException, SAXException, IOException {
		File cve_dir = new File("cvesample");
		cvedb = new CVELookUp(cve_dir, true);
		List<CVEInfo> result = cvedb.lookup("linux_kernel:2.6.20.1");            

		assertFalse(result.isEmpty());
		result.forEach(System.out::println);
	}
	*/
	
	@Test
	public void buildDB() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		//File cveDir = new File("cvesample");
		//File cveDir = new File("/ufo/cve");
		//CVEUtils.buildDB(cveDir, "192.168.1.107:9200");
	}


}
