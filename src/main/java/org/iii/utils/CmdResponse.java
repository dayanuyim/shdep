package org.iii.utils;

public class CmdResponse {
	private int exitCode;
	private String stdout;
	private String stderr;
	
	public int getExitCode() {
		return exitCode;
	}

	public String getStdout() {
		return stdout;
	}

	public String getStderr() {
		return stderr;
	}

	public CmdResponse(int exitCode, String stdout, String stderr){
		this.exitCode = exitCode;
		this.stdout = stdout;
		this.stderr = stderr;
	}

}
