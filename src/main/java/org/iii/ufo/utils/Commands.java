package org.iii.ufo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.iii.utils.CmdResponse;
import org.iii.utils.Command;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.*;

public class Commands {
	private static final Logger logger = LoggerFactory.getLogger(Commands.class);

	public static JSONObject shfmt(Path script, boolean tryOnlyAscii) throws IOException, InterruptedException{
		try{
			return _shfmt(script, false);
		}
		catch (Exception e) {
			//second change to rescure: remove non-ascii char
			if(tryOnlyAscii && e.getMessage().contains("invalid UTF-8 encoding")){
				logger.warn("shfmt error: {}. Removing non-ascii char, and then try.", e.getMessage());
				return _shfmt(script, true);
			}
			throw e;
		}
	}

	private static JSONObject _shfmt(Path script, boolean tryOnlyAscii) throws IOException, InterruptedException{
		String cmd = tryOnlyAscii?
			String.format("perl -pe 's/[^[:ascii:]]//g' '%s' | shfmt -exp.tojson", script.toAbsolutePath().toString()):
			String.format("cat '%s' | shfmt -exp.tojson", script.toAbsolutePath().toString());
		String stdout = Command.cmd(cmd).check().run().getStdout();
		return new JSONObject(stdout);
	}

	public static JSONObject shfmt(String str) throws IOException, InterruptedException{
		String cmd = String.format("echo -n '%s' | shfmt -exp.tojson", str);
		String stdout = Command.cmd(cmd).check().run().getStdout();
		return new JSONObject(stdout);
	}
	
	public static Path getHomeDir(String user) throws IOException, InterruptedException{
		Path pwdfile = Paths.get("/etc/passwd");
		return getHomeDir(pwdfile, user);
	}

	public static Path getHomeDir(Path pwdfile, String user) throws IOException, InterruptedException{
		String cmd = String.format("grep '%s' -e '^%s:' | cut -d':' -f6", pwdfile.toAbsolutePath(), user);
		String stdout = Command.cmd(cmd).check().run().getStdout();

		return isEmpty(stdout)? null: Paths.get(stdout);
	}

	public static String fileMagic(File f) throws IOException, InterruptedException{
		String cmd = String.format("file --brief '%s'", f.getAbsolutePath());
		//logger.info(cmd);
		CmdResponse resp = Command.cmd(cmd).check().run();
		return resp.getStdout().trim();
	}

}
