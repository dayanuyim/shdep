import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Tmp {

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
	public void test() throws IOException {
		final File passwd = new File("/etc/passwd");
		final File tmp = new File("/tmp/xxx");
		final File linkedPasswd = new File(tmp.getParent(), "../etc/passwd");
		assertEquals(passwd, linkedPasswd.toPath().toRealPath().toFile());

		Pattern pattern = Pattern.compile(".*%%(.*)-root%%.*");
		//File conf = new File("/usr/local/lib/python2.7/dist-packages/binwalk/config/extract.conf");
		File conf = new File("/usr/lib/python3/dist-packages/binwalk/config/extract.conf");
		FileUtils.readLines(conf, StandardCharsets.UTF_8).stream()
		.map(line->{
			Matcher matcher = pattern.matcher(line);
			if(matcher.find())
				return matcher.group(1);
			return null;
		})
		.filter(t->t!=null)
		.collect(Collectors.toSet())
		.forEach(System.out::println);
	}
	
	public static String kernelVersion(String str){
		Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+(\\.\\d)?).*");
		Matcher matcher = pattern.matcher(str);
		if(matcher.find()){
			System.out.println(String.format("1: %s, 2: %s", matcher.group(1), matcher.group(2)));
			return matcher.group(1);
		}
		return null;
	}

	@Test
	public void test2(){
		assertEquals("2.6.18", kernelVersion("2.6.18_pro500"));
		assertEquals("2.6.20.2", kernelVersion("2.6.20.2 (zhaoyuanbiao@ubuntu)"));
	}

	@Test
	public void test3(){

		Path path = Paths.get("/bar", "foo", "xxx");
		System.out.println("path '" + path + "' starts with /bar: " + path.startsWith("/bar"));
	}
}
