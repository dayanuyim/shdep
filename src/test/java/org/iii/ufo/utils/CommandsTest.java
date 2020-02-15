package org.iii.ufo.utils;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.iii.ufo.entity.FilePattern;
import org.iii.utils.Command;
import org.iii.ufo.utils.Commands.GrepArg;
import org.iii.utils.TempPath;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandsTest {

	private static final Logger logger = LoggerFactory.getLogger(CommandsTest.class);

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
	public void test_7z() throws IOException, InterruptedException {

		try(TempPath testFile = new TempPath(Paths.get("/tmp"), "ufo-", "")) {
			try (Writer writer = Files.newBufferedWriter(testFile.toPath())) {
				writer.write("deadbeaf");
			}

            File zip = Commands._7zArchive(testFile.toFile());

            assert(zip.exists() &&
                   zip.isFile() &&
                   zip.length() > 0 &&
                   zip.getName().endsWith(".7z") &&
                   zip.getParentFile().equals(testFile.toFile().getParentFile()));  // is  the same dir
		}
	}

	@Test
	public void test_mimetype() throws IOException, InterruptedException {
		File passwd = new File("/etc/passwd");
		String mime = Commands.mimetype(passwd);
		assertEquals("text/plain", mime);

		File ls = new File("/bin/ls");
		mime = Commands.mimetype(ls);
		assertEquals("application/x-sharedlib", mime);
	}

	@Test
	public void test_findName() throws IOException, InterruptedException {
		File etc = new File("/etc");
		List<File> files = Commands.findName(etc, "pass*");

		assert(!files.isEmpty());

		for(File f: files){
			assert(f.exists());
			//System.out.println(f.getAbsolutePath());
		}
	}

	@Test
	public void test_findMagic_dir() throws IOException, InterruptedException {

		File f = new File("/etc");
		String type = Commands.fileMagic(f);
		assertEquals("directory", type);
	}

	@Test
	public void test_findMagic_binary() throws IOException, InterruptedException {
		File f = new File("/bin/ls");
		String type = Commands.fileMagic(f);
		assert(type.contains("ELF"));
		//assert(type.contains("executable"));
	}

	@Test
	public void test_findMagic_passwd() throws IOException, InterruptedException {
		File f = new File("/etc/passwd");
		String type = Commands.fileMagic(f);
		assertEquals("ASCII text", type);
	}

	@Test
	public void test_ssdeep() throws IOException, InterruptedException {
		File f = new File("/etc/passwd");
		String hash = Commands.ssdeep(f);
		assert(StringUtils.isNotBlank(hash));
	}
	

	@Test
	public void test_trid() throws IOException, InterruptedException {
		File f = new File("/bin/ls");
		List<String> ids = Commands.trid(f);
		ids.forEach(System.out::println);
		assert(!ids.isEmpty());
	}

	public static void writePseudoPasswd(Path testFile) throws IOException {
		try (Writer writer = Files.newBufferedWriter(testFile)) {
			writer.write("jason:$1$inyK1$bqgTfMo2Px8F8OByOoaUf1:1000:1000:jason,lablab,,:/home/jason:/bin/bash");
		}
	}

	public static void writePseudoWrongPasswd(Path testFile) throws IOException {
		try (Writer writer = Files.newBufferedWriter(testFile)) {
			writer.write("jason:$1$inyK1$bqgTfMo2Px8F8OByOoaUfx:1000:1000:jason,lablab,,:/home/jason:/bin/bash");
		}
	}

	@Test
	public void test_johnSingle_hit() throws IOException, InterruptedException {
		try(TempPath testFile = new TempPath(Paths.get("/tmp"), "ufo-", ""))
		{
			writePseudoPasswd(testFile.toPath());

			Map<String, String> resutl = Commands.johnSingle(testFile.toFile());
			assertEquals(1, resutl.size());

			resutl.forEach((name, pass) -> {
				assertEquals("jason", name);
				assertEquals("lablab", pass);
			});
		}
	}

	@Test
	public void test_johnSingle_concurrecy() throws IOException, InterruptedException {

		try(TempPath testFile = new TempPath(Paths.get("/tmp"), "ufo-", ""))
		{
			writePseudoWrongPasswd(testFile.toPath());

			List<Thread> threads = new ArrayList<>();
			for(int i = 0; i < 10; ++i)
				threads.add(new Thread(()->{
					try {
						Commands.johnSingle(testFile.toFile());
					} catch (Exception e) {
						logger.error("run john error: {}", ExceptionUtils.getStackTrace(e));
					}
				}));

			threads.forEach(Thread::start);
			threads.forEach(t -> {
				try {
					t.join();
				} catch (InterruptedException e) {
					logger.error("join john error: {}", ExceptionUtils.getStackTrace(e));
				}
			});
		}
	}

	@Test
	public void test_johnSingle_notHit() throws IOException, InterruptedException {

		try(TempPath testFile = new TempPath(Paths.get("/tmp"), "ufo-", "")){
		    writePseudoWrongPasswd(testFile.toPath());

			Map<String, String> resutl = Commands.johnSingle(testFile.toFile());
			assertEquals(0, resutl.size());
		}
	}

	@Test
	public void test_johnWordlist_hit() throws IOException, InterruptedException {

		try(TempPath testPasswd = new TempPath(Paths.get("/tmp"), "ufo-", "");
			TempPath testWordlist = new TempPath(Paths.get("/tmp"), "ufo-", ""))
		{
			writePseudoPasswd(testPasswd.toPath());
			try(Writer writer = Files.newBufferedWriter(testWordlist.toPath())){
				writer.write("lablab");
			}

			Map<String, String> resutl = Commands.johnWordlist(testPasswd.toFile(), testWordlist.toFile());
			assertEquals(1, resutl.size());

			resutl.forEach((name, pass) -> {
				assertEquals("jason", name);
				assertEquals("lablab", pass);
			});
        }

	}

	@Test
	public void test_johnWordlist_nothit() throws IOException, InterruptedException {

		try(TempPath testPasswd = new TempPath(Paths.get("/tmp"), "ufo-", "");
			TempPath testWordlist = new TempPath(Paths.get("/tmp"), "ufo-", ""))
		{
			writePseudoWrongPasswd(testPasswd.toPath());
			try(Writer writer = Files.newBufferedWriter(testWordlist.toPath())){
				writer.write("lablab");
			}

			Map<String, String> result = Commands.johnWordlist(testPasswd.toFile(), testWordlist.toFile());
			assertEquals(0, result.size());
		}
	}

	@Test
	public void test_egrepPattern() throws IOException, InterruptedException {
		Path tmp = Files.createTempDirectory(Paths.get("/tmp"), "ufo-");
		try{
			Path expFile = tmp.resolve("test");
			String expLine = "jason:$1$inyK1$bqgTfMo2Px8F8OByOoaUfx:1000:1000:jason,,,:/home/jason:/bin/bash";
			String expPattern = expLine.split(":")[1];
			FileUtils.write(expFile.toFile(), expLine, "UTF-8");

			String pattern = "\\$1\\$\\w{5}\\S{23}";

			//test grep line ======================================
			List<FilePattern> founds = Commands.egrepPattern(tmp.toFile(), pattern);
			assertEquals(1, founds.size());
			assertEquals(expFile.toFile(), founds.get(0).getFile());
			assertEquals(expLine, founds.get(0).getPattern());

			//test grep pattern ======================================
			founds = Commands.egrepPattern(tmp.toFile(), pattern, GrepArg.OnlyMatch);
			assertEquals(1, founds.size());
			assertEquals(expFile.toFile(), founds.get(0).getFile());
			assertEquals(expPattern, founds.get(0).getPattern());
		}
		finally {
			FileUtils.deleteDirectory(tmp.toFile());
		}
	}
	
	@Test
	public void test_readelfHeader() throws IOException, InterruptedException{
		File exec = new File("/bin/ls");
		Map<ElfHeader, String> header = Commands.readelfHeader(exec);

		assertEquals(ElfHeader.values().length, header.size());
		header.forEach((key,val)-> logger.info("[{}] {}", key, val));
	}

	@Test
	public void test_qemuExec() throws IOException, InterruptedException {
		//create tmp dir
		Path tmp = Files.createTempDirectory(Paths.get("/tmp"), "ufo-");
		try{
			//unzip test data
			File root_7z = new File("src/test/resources/squashfs-root.7z");
			Commands._7zExtract(root_7z, tmp.toFile());
			logger.info("unzip test data {} to {}", root_7z, tmp);

			//qemu exec
			Path root = tmp.resolve("squashfs-root");
			Path exec = tmp.resolve("squashfs-root/usr/sbin/dropbear");
			String result = Commands.qemuExec(root.toFile(), exec.toFile(), "-h").getStderr();
			System.out.println(result);
		}
		finally {
			FileUtils.deleteDirectory(tmp.toFile());
		}
	}
	
	@Test
	public void test_shfmt() throws IOException, InterruptedException{
		try (TempPath script = new TempPath(Paths.get("/tmp"), "ufo-", ".sh")){
			try (Writer writer = Files.newBufferedWriter(script.toPath())) {
				writer.write("ls /");
			}
			JSONObject obj = Commands.shfmt(script.toPath(), true);
			assertTrue(obj.getJSONObject("StmtList") != null);
		}
	}

	@Test
	public void test_getHomeDir() throws IOException, InterruptedException{
		Path home = Commands.getHomeDir("root");
		Path expHome = Paths.get("/root");   //should be true almost, but not MUST!
		assertEquals(expHome, home);
	}
	
	@Test
	public void test_timeoutHangForStdin() throws IOException, InterruptedException
	{
		Exception catchedEx = null;
		try{
			String cmdline = "echo \"$(cat $1 | tr -d \\')\"";   //this cannot timeout, even prefix 'timeout --signal=SIGKILL 10'

			logger.info("run external cmd: {}", cmdline);
			Command.cmd(cmdline).timeout(Duration.ofSeconds(3)).check().run().getStdout();
		}
		catch (Exception e) {
			logger.info("run cmd error: " + e.getMessage());
			catchedEx = e;
		}

		//show throw timeout exception
		assertTrue(catchedEx != null && catchedEx.getMessage().contains("timeout"));
	}

	@Test
	public void test_getGccOptions() throws IOException, InterruptedException {
		List<String> options = Commands.getGccOptions(Paths.get("/bin/ls"));
		assertEquals(2, options.stream()
								.filter(s -> s.equals("-fPIE") || s.equals("-fstack-protector"))
								.count());
	}
}
