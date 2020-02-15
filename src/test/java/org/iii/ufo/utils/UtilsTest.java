package org.iii.ufo.utils;

import static org.iii.ufo.utils.Utils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.iii.utils.Command;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtilsTest {

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
	public void runCommand_test() throws IOException, InterruptedException {
		String accounts = Command.cmd("cat /etc/passwd").check().run().getStdout();
		
		boolean has_root = false;
		for(String account: accounts.split("\n"))
			if(account.startsWith("root:"))
				has_root = true;

		assertTrue(has_root);
	}

	@Test(expected=RuntimeException.class)
	public void runCommand_testFailedCmd() throws IOException, InterruptedException {
		Command.cmd("nonexist-cmd").check().run();
	}

	@Test
	public void runCommand_testFailedArgs() throws IOException, InterruptedException {
		assertNotEquals(0, Command.cmd("ls '/nonexist-dir'").run().getExitCode());
	}

	@Test
	public void runCommand_testPipe() throws IOException, InterruptedException {
		System.out.println(Command.cmd("ls / | wc -l").run().getStdout());
	}
	
	@Test
	public void getExecutableDir_test(){
		File wd = getExecutableDir();
		assertTrue(wd.exists());
		assertTrue(wd.isDirectory());
	}

	@Test
	public void matches_test(){
		String str = "hello.123";
		assert(str.matches(".*\\.123$"));
	}
}
