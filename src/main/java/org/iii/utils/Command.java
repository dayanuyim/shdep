package org.iii.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.*;

public class Command {
	
	private static final Logger logger = LoggerFactory.getLogger(Command.class);
	
	public static Command cmd(String cmd){
		return new Command(cmd);
	}
	
	private final String cmd;
	//private File outfile;
	private String stdout;
	private String stderr;
	private Set<Integer> expExitCodes = new HashSet<>();
	private boolean shouldCheckStderr = false;
	private Object syncObj;
	private Duration timeout;
	
	private Command(String cmd){
		this.cmd = cmd;
	}
	
	/*
	public Command redirect(File f){
		this.outfile = f;
		return this;
	}
	*/
	
	//timeout =============
	public Command timeout(Duration timeout){
		this.timeout = timeout;
		return this;
	}

	//normal case
	public Command check(){
		return expExitCodes(0).checkStderr();
	}
	
	public Command expExitCodes(int...ec){
		for(int code: ec)
			expExitCodes.add(code);
		return this;
	}
	
	public Command checkStderr(){
		this.shouldCheckStderr = true;
		return this;
	}
	
	/*
	public static String runCommand(String... command) throws IOException{
		
		Process process = new ProcessBuilder(command).start();

		try(InputStream istream = process.getInputStream()){
			return IOUtils.toString(istream, "UTF-8");
		}
	}
	*/
	
	public Command sync(Object syncObj){
		this.syncObj = syncObj;
		return this;
	}

	public CmdResponse run() throws IOException, InterruptedException
	{
		int exitCode = (syncObj != null)? syncExec(): exec();

		//check error
		if( !legalExitCode(exitCode) || !legalStderr()) {
			//some command print err using stdout
			String errmsg = stderr.isEmpty()? stdout: stderr;
			logger.error("run '{}', exitCode: {}, expExitCode: {}, error: {}", cmd, exitCode, expExitCodes, errmsg);
			throw new RuntimeException(errmsg);
		}
		
		//Response
		return new CmdResponse(exitCode, stdout, stderr);
	}

	private boolean legalExitCode(int exitCode){
		return expExitCodes.isEmpty() || expExitCodes.contains(exitCode);
	}

	private boolean legalStderr(){
		return !shouldCheckStderr || isEmpty(stderr);
	}
	
	private int syncExec() throws IOException, InterruptedException{
		synchronized (syncObj) {
			return exec();
		}
	}
	
	private int exec() throws IOException, InterruptedException{
		Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cmd});
		//@@! This method process.waitFor() is handy, but will block forever if stdout is huge and not read it out.
		//int exitCode = process.waitFor();
		return waitAndReadOutput(process);
	}
	
	
	
	/*
	private static void redirectStdout(Process process, File outfile) throws IOException{
		try(InputStream stdout = process.getInputStream()){
			FileUtils.copyToFile(stdout, outfile);
		}
	}

	private static String getStdout(Process process) throws IOException{
		try(InputStream stdout = process.getInputStream()){
			return IOUtils.toString(stdout, "UTF-8");
		}
	}
	
	private static String getStderr(Process process) throws IOException{
		try(InputStream stderr = process.getErrorStream()){
			return IOUtils.toString(stderr, "UTF-8");
		}
	}
	*/
	
	private int waitAndReadOutput(Process process) throws IOException, InterruptedException{
		
		LocalDateTime due = (timeout == null)? null: LocalDateTime.now().plus(timeout);
		
		try(InputStream stdoutIstream = process.getInputStream();
			InputStream stderrIstream = process.getErrorStream();
			OutputStream stdoutOstream = new ByteArrayOutputStream();
			OutputStream stderrOstream = new ByteArrayOutputStream()){

			byte[] buf = new byte[8192];
			boolean isDie = false;
			for(;;){

				while(stdoutIstream.available() > 0){
					int n = stdoutIstream.read(buf, 0, buf.length);
					stdoutOstream.write(buf, 0, n);
				}

				while(stderrIstream.available() > 0){
					int n = stderrIstream.read(buf, 0, buf.length);
					stderrOstream.write(buf, 0, n);
				}

				if(isDie)
					break;
				
				//reach timeout
				if(due != null && LocalDateTime.now().isAfter(due)){
					process.destroy();
					throw new RuntimeException("run command [" + cmd + "] timeout");
				}

				isDie = process.waitFor(100, TimeUnit.MILLISECONDS);
			}
			
			stdout = trim(stdoutOstream.toString());
			stderr = trim(stderrOstream.toString());
		}
		
		return process.exitValue();
	}

}
