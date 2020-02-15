package org.iii.ufo.shdep;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iii.ufo.shdep.nodes.cmd.CallExpr;
import org.iii.ufo.shdep.nodes.cmd.FuncDecl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecVisitor extends NodeVisitorAdapter{
	public enum CmdType{
		Builtin, RelPath, AbsPath, Path, Func;
	}

	public static Set<String> builtinCmds = new HashSet<>(Arrays.asList(
			"((", ".", ":", "[", "[[", "alias", "bg", "bind", "break", "builtin", "caller",
			"case", "cd", "command", "compgen", "complete", "compopt", "continue", "coproc",
			"declare", "dirs", "disown", /*"echo",*/ "enable", "eval", "exec", "exit", "export",
			"false", "fc", "fg", "for", "for", "function", "getopts", "hash", "help", "history",
			"if", "jobs", /*"kill",*/ "let", "local", "logout", "mapfile", "popd", "printf", "pushd",
			"pwd", "read", "readarray", "readonly", "return", "select", "set", "shift", "shopt",
			"source", "suspend", "test", "time", "times", "trap", "true", "type", "typeset", "ulimit",
			"umask", "unalias", "unset", "until", "variables", "wait", "while"));

	public CmdType cmdType(String cmd){
		if(funcTable.containsKey(cmd))
			return CmdType.Func;

		if(builtinCmds.contains(cmd))
			return CmdType.Builtin;

		if(cmd.contains("/"))
			return cmd.startsWith("/")? CmdType.AbsPath: CmdType.RelPath;
		
		return CmdType.Path;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExecVisitor.class);

	private final File root;
	private final File pwd;
	private final List<File> path;
	private final Map<String, FuncDecl> funcTable;
	
	public ExecVisitor(File root, File pwd, List<File> path, Map<String, FuncDecl> funcTable){
		this.root = root;
		this.pwd = pwd;
		this.path = path;
		this.funcTable = funcTable;
	}
	
	//search @exec file under PATH, like 'which' cmd
	private File prefixEnvPath(String exec){
		for(File p: path){
			File execFile = new File(p, exec);
			if(execFile.exists())
				return execFile;
		}
		return null;
	}

	@Override
	public void visit(CallExpr cmd) {
		if(cmd.getArgs().isEmpty())
			return;

		if(cmd.readArg(0).equals("[") && cmd.readArg(-1).equals("]")){
			String exec = cmd.readArg(1);
			if(exec.equals("!"))
				exec += " " + cmd.readArg(2);
			
			//TODO handle builtin cmd
			logger.info(exec);
		}
		else{
			String exec = cmd.readArg(0);

			switch (cmdType(exec)) {
			case Builtin:
				logger.info("<builtin> {}", exec);
				logger.info("<function> {}", exec);
				logger.info("<cmdfile> {}", exec);
				break;
			case AbsPath:{
				File execFile = new File(root, exec);
				logger.info(execFile.getAbsolutePath());
				break;
			}
			case RelPath:{
				File execFile = new File(pwd, exec);
				logger.info(execFile.getAbsolutePath());
				break;
			}
			case Path:{
				File execFile = prefixEnvPath(exec);
				if(execFile == null)
					logger.error("Cmd not found: {}", exec);
				else
					logger.info(execFile.getAbsolutePath());
				break;
			}
			case Func:
				logger.info("<func> {}", exec);
				break;
			default:
				logger.error("Unknown cmd type: {}", cmdType(exec));
			}
		}
	}
}

