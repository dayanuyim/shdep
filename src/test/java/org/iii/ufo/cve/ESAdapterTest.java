package org.iii.ufo.cve;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import org.iii.ufo.cve.CVEInfo;
import org.iii.ufo.cve.ESAdapter;
import org.iii.ufo.cve.ProductInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ESAdapterTest {
	
	private ESAdapter adapter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		adapter = new ESAdapter("127.0.0.1", 9200);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private static void print(List<CVEInfo> cves){
		System.out.println(cves.size());
		
		for(ListIterator<CVEInfo> it = cves.listIterator(); it.hasNext();){
			int i = it.nextIndex();
			CVEInfo info = it.next();
			System.out.println();
			System.out.println("[" + i + "] " + info.getCveid());
			info.getProducts().forEach(System.out::println);
		}
	}

	@Test
	public void test_searchCVE_Openssh73() throws IOException, InterruptedException {
		List<CVEInfo> cves = adapter.searchCVE(new ProductInfo("openssh", "7.3"));
		assertTrue(!cves.isEmpty());
		print(cves);
	}

	@Test
	public void test_searchCVE_Openssh50p1() throws IOException, InterruptedException {
		List<CVEInfo> cves = adapter.searchCVE(new ProductInfo("openssh", "5.0", "p1"));
		assertTrue(!cves.isEmpty());
		print(cves);
	}

	@Test
	public void test_searchCVE_Openssh44p1() throws IOException, InterruptedException {
		List<CVEInfo> cves = adapter.searchCVE(new ProductInfo("openssh", "4.4p1"));
		assertTrue(!cves.isEmpty());
		print(cves);
	}

	@Test
	public void test_searchCVE_LinuxKernel() throws IOException, InterruptedException {
		List<CVEInfo> cves = adapter.searchCVE(new ProductInfo("linux_kernel", "2.6.20.1"));
		assertTrue(!cves.isEmpty());
		print(cves);
	}
}
