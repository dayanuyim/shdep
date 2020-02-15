package org.iii.ufo.shdep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.iii.ufo.shdep.nodes.Assign;
import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.Op;
import org.iii.ufo.shdep.nodes.Redirect;
import org.iii.ufo.shdep.nodes.ScriptFile;
import org.iii.ufo.shdep.nodes.Stmt;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.ufo.shdep.nodes.cmd.CallExpr;
import org.iii.ufo.shdep.nodes.cmd.DeclClause;
import org.iii.ufo.shdep.nodes.cmd.FuncDecl;
import org.iii.ufo.shdep.nodes.parts.CmdSubst;
import org.iii.ufo.shdep.nodes.parts.Lit;
import org.iii.ufo.shdep.nodes.parts.ParamExp;
import org.iii.ufo.shdep.nodes.parts.WordPart;
import org.iii.ufo.utils.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.iii.utils.CommonUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

public class Shell {

	private static final Logger logger = LoggerFactory.getLogger(Shell.class);
	
	public static final String DEF_USR = "root";
	public static String DEF_PATHLINE = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin";
	
	public static Set<String> builtinCmds = new HashSet<>(
			Arrays.asList("((", ".", ":", "[", "[[", "alias", "bg", "bind", "break", "builtin", "caller", "case", "cd",
					"command", "compgen", "complete", "compopt", "continue", "coproc", "declare", "dirs", "disown",
					/* "echo", */ "enable", "eval", "exec", "exit", "export", "false", "fc", "fg", "for", "for",
					"function", "getopts", "hash", "help", "history", "if", "jobs", /* "kill", */ "let", "local",
					"logout", "mapfile", "popd", "printf", "pushd", "pwd", "read", "readarray", "readonly", "return",
					"select", "set", "shift", "shopt", "source", "suspend", "test", "time", "times", "trap", "true",
					"type", "typeset", "ulimit", "umask", "unalias", "unset", "until", "variables", "wait", "while"));
	
	//The external commands which allow to simulate.
	private static Set<String> simExterCmds = new HashSet<>(Arrays.asList(
			/*"cat",*/ "echo", "awk", "sed", "grep", "dirname", "basename", "file", "wc"));

	private static final String PATH = "PATH";
	private static final String CWD = "PWD";
	
	private static int curr_pid = 1000; 
	
	public static int getNextPid(){ return curr_pid++;}

	private final Path fsroot;
	private final ShellVar shellVar = new ShellVar();
	private final Map<String, Func> funcTable = new HashMap<>();
	private List<FsPath> envPath; // maintain inner env PATH
	private ShellHook hook;
	
	/*
	 * public Shell(String pwd, String envPath, String fsroot){ this.fsroot =
	 * (fsroot == null)? null: new File(fsroot); this.pwd = chroot(this.fsroot,
	 * pwd); this.envPath = Stream.of(envPath.split(":"))
	 * .map(p->chroot(this.fsroot, p)) .collect(Collectors.toList()); }
	 */
	
	public Shell(Path fsroot) {
		this.fsroot = fsroot;
		initCommon();
		initFirst();
	}

	//init from parent
	private Shell(Shell parent) {
		this.fsroot = parent.getFsroot();
		initCommon();
		initFromParent(parent);
	}
	
	private void initCommon(){
		//pid
		shellVar.set("$", getNextPid());
		logger.info("Shell pseudo pid = {}", shellVar.get("$"));

		//last cmd's exit code
		shellVar.set("?", "0");  // assume $? as 0

		//set default action 
		shellVar.setFunc(PATH, (name, pathline)->{
			logger.info("update $PATH to {}", pathline);
			this.envPath = StringUtils.isEmpty(pathline) ? Collections.emptyList():
						Stream.of(pathline.split(":"))
							.filter(StringUtils::isNoneBlank)
							.map(p -> FsPath.of(fsroot, p))
							.collect(Collectors.toList());
		});
	}

	private void initFirst(){
		//pseudo-env
		shellVar.set("USER", DEF_USR, true);
		shellVar.set("HOME", getHomeDir().getInnerPath().toString(), true);
		shellVar.set(CWD, shellVar.get("HOME"), true);

		//source profile
		try {
			run("source /etc/profile");
		}
		catch (Exception e) {
			logger.error("source /etc/profile error: {}", e.getMessage());
		}

		//check if PATH is set in /etc/profile
		String pathline = shellVar.get(PATH);
		if(pathline != null){
			shellVar.export(PATH);   //export anyway
			logger.info("$PATH is initialized to '{}'", pathline);
		}
		if(isEmpty(pathline)){
			shellVar.set(PATH, DEF_PATHLINE, true);
			logger.info("$PATH is set default to '{}'", DEF_PATHLINE);
		}
		
	}
	
	private void initFromParent(Shell parent){
		//env
		Map<String, String> env = parent.shellVar.getEnv();
		shellVar.setEnv(env);
		
	}

	public Shell newShell() {
		Shell shell = new Shell(this);  //copy env
		shell.setHook(this.hook);
		return shell;
	}

	/*
	private void updateEnvPath() {
		String pathline = shellVar.get(PATH);
		this.envPath = StringUtils.isEmpty(pathline) ? Collections.emptyList():
					Stream.of(pathline.split(":"))
						.map(p -> FsPath.of(fsroot, p))
						.collect(Collectors.toList());
	}
	*/
	
	public Path getFsroot(){
		return fsroot;
	}
	
	public ShellHook getHook() {
		return hook;
	}

	public void setHook(ShellHook hook) {
		this.hook = hook;
	}
	
	// run interface ==================================================================
	
	// cmdline is special case, whicn run a anonymous script, only single cmdline,  without args
	public Exec run(String cmdline) throws IOException, InterruptedException{
		funcTable.clear();

		Node ast = new ScriptFile(Commands.shfmt(cmdline));
		return _run(null, ast, Collections.emptyList()).get(0);
	}

	public List<Exec> run(Script script) { return run(script,Collections.emptyList()); }
	public List<Exec> run(Script script, List<Word> args) {
		funcTable.clear();
		return _run(script, args);
	}
	
	//run ==================================================================================

	private List<Exec> _run(Script script, List<Word> args) {
		if(hook != null){
			List<String> strArgs = args.stream()
									.map(arg->arg.toString())
									.collect(Collectors.toList());

			if(!hook.beforeScript(script, strArgs))
				return Collections.emptyList();
		}

		return _run(script.getPath(), script.getAST(), args);
	}
	
	// @shpath is unnecessary, only for tracking where a function is delared
	// @ast is the AST presentation for the script
	// @args is optional, also in the AST form
	private List<Exec> _run(FsPath shpath, Node ast, List<Word> args) {
		// add to table
		// scripts.put(file, scriptFile);
		//logger.info("run script '{}': \n{}", shpath, ast);

		List<Exec> execs = new ArrayList<>();

		ast.accept(new NodeVisitorAdapter() {
			// build func table
			@Override
			public void visit(FuncDecl decl) {
				funcTable.put(decl.getName().toString(), new Func(shpath, decl));
			}
			
			//export
			@Override
			public void visit(DeclClause decl) {
				super.visit(decl);

				if(decl.getVariant().getValue().equals("export")){
					for(Assign assign: decl.getAssigns()){
						logger.warn("export variable {}", assign.getName());
					}
				}
			}

			//handle variables setting
			@Override
			public void visit(Assign assign){
				super.visit(assign);

				//logger.info("<assign> {}={}", assign.getName(), assign.getValue());

				// just skip naked assign, which no value
				if(!assign.isNaked()){
					//logger.info("...{}", assign);
					String var = assign.getName().toString();
					String value = String.format("%s%s", 
							assign.isAppend()? defaultString(shellVar.get(var)): "",
							strip(strip(assign.getValue().toString(), "\""), "'"));
					shellVar.set(var, value);
				}
			}

			//Variable Apply
			@Override
			public void visit(Word word){
				super.visit(word);
				
				for(ListIterator<WordPart> it = word.getParts().listIterator(); it.hasNext();){
					WordPart part = it.next();
					it.set(evalWardPart(part, args));
				}
			}
			
			//Beacause redirection is handle by Stmt, not CallExpr, so handle exec in here
			@Override
			public void visit(Stmt stmt){
				logger.debug("run stmt..." + stmt);
				super.visit(stmt);
				
				if(!(stmt.getCmd() instanceof CallExpr))
					return;

				Exec exec = toExec(stmt);
				if(exec == null) //ex: the stmt is assign expression, or no cmd name
					return;
				
				if(exec.getCmdpath() != null && exec.getCmdpath().getInnerPath().toString().equals("/")){
					logger.error("!!!! invalid innerpath: {}", stmt);
					System.exit(1);
				}

				switch (exec.getType()) {
				case ShellOp:
					// TODO handle builtin cmd
					logger.debug("<{}>{}", exec.getType(), exec);
					break;
				case Func:
					logger.debug("<{}> {}", exec.getType(), exec);
					execs.add(exec);

					if(!exec.getFunc().isTouched()){
						exec.getFunc().touch();
						Node funcBody = exec.getFunc().getDecl().getBody();
						exec.addChildren(_run(null, funcBody, exec.getArgs()));
					}
					break;
				case Builtin:
					logger.debug("<{}> {}", exec.getType(), exec);
					execs.add(exec);
					runBuiltinCmd(exec, args);
					break;
				case AbsPath:
				case RelPath:
				case EnvPath:
					execs.add(exec);
					if (exec.getAbsPath() == null || !Files.exists(exec.getAbsPath())) {
						logger.error("Cmd not found: {}", exec.getAbsPath());
					}
					else if (isShellScript(exec.getAbsPath())) {
						logger.debug("<{}> {}", exec.getType(), exec.getAbsPath());

						try {
							Script script = new Script(exec.getCmdpath());
							exec.addChildren(newShell().run(script, exec.getArgs()));
						} catch (Exception e) {
							logger.error("pseudo run script '{}' error: {}", exec, getStackTrace(e));
						}
					} else {
						logger.debug("<{}> {}", exec.getType(), exec.getAbsPath());
					}
					break;
				default:
					logger.error("Unknown cmd type: {}", exec.getType());
				}
			}
		});

		return execs;
	}
	
	private WordPart evalWardPart(WordPart part, List<Word> args){
		if(part instanceof ParamExp){   //is variable
			String var = ((ParamExp)part).getParam().getValue();

			String value = null;
			if(NumberUtils.isDigits(var)){
				int i = Integer.parseInt(var);
				if(args.size() > i)
					value = args.get(i).toString();
			}
			//args number, except the args[0]
			else if(var.equals("#")){
				value = "" + Math.max(0, args.size() -1);
			}
			else{
				value = shellVar.get(var);
			}
			
			if(value == null)
				logger.error("nonexist variable: {}", var);
			else{
				//replace by literal
				//logger.info("replace variable {} -> {}", var, value);
				return new Lit(value);
			}
		}
		// It's a favor to replace the 'cmdSubst' by literal string. in NAiVE way
		// Strictly, should use qemu to run 
		else if(part instanceof CmdSubst){
			
			CmdSubst subst = (CmdSubst)part;
			if(isAllowedExterCmds(subst)){
				String cmdline = subst.toCmdString();
				Duration tmo = Duration.ofSeconds(10);
				try {
					logger.info("run external cmd: {}", cmdline);
					String value = org.iii.utils.Command.cmd(cmdline).timeout(tmo).check().run().getStdout();
					return new Lit(value);
				}
				catch (Exception e) {
					logger.info("run cmd '{}' error: {}", cmdline, getStackTrace(e));
				}
			}
		}

		return part;
	}
	
	// TODO to fix: not allowed sub cmd will be executed, like: 'echo $(tr '\0' ' ')' < /path/to/file'
	private boolean isAllowedExterCmds(CmdSubst subst){

		final boolean[] allowed = new boolean[]{true, true}; //workaound for setting in anonymous class

		subst.accept(new NodeVisitorAdapter() {
			@Override
			public void visit(Redirect redir){
				allowed[0] = allowed[0] && (redir.getOp() != Op.rdrOut);
			}

			@Override
			public void visit(CallExpr expr){
				allowed[1] = allowed[1] && simExterCmds.contains(expr.readArg(0));
			}
		});
		
		if(!allowed[0]) logger.warn("not allowd cmd '{}' due to output redirect", subst);
		if(!allowed[1]) logger.warn("not allowd cmd '{}' due to not supported cmd", subst);
		
		return BooleanUtils.and(allowed);
	}
	
	private void runBuiltinCmd(Exec exec, List<Word> args){
		switch(exec.getCmd()){
		case ".":
		case "source":
			FsPath src = exec.getCmdpath();
			if (src == null || !src.isRegularFile()){
				logger.info("invalid source arg: {}", src);
				break;
			}

			try {
				Script script = new Script(src);
				exec.addChildren(_run(script, args));  //invoke inner '_run'
			} catch (Exception e) {
				logger.error("source '{}' error: {}", src.getFullPath(), getStackTrace(e));
			}
			break;
		case "cd":
			String oldcwd = shellVar.get(CWD);
			String cwd = exec.readArg(1);
			if(!isAbsPath(cwd))
				cwd = Paths.get(oldcwd, cwd).toString();  //combine as new cwd
			shellVar.set(CWD, cwd);
			logger.info("change cwd: '{}' -> '{}'", oldcwd, cwd);
			break;
		case "read":
			String var = exec.readArg(1);
			//String value = StringUtils.join(restSubList(exec.getArgs(), 2), " ");
			shellVar.set(var, "%" + exec.toString() + "%");    //TODO any improve but just set to empty string?
			logger.warn("read variable '{}' ({})", var, exec);
			break;
		default:
			//logger.warn("unknown builtin cmd '{}'", exec.getCmd());
			break;
		}
	}


	//assume @stmt's cmd is CallExpr
	private Exec toExec(Stmt stmt) {// , Map<String,FuncDecl> funcTable){

		//get command name
		if(!(stmt.getCmd() instanceof CallExpr))
			return null;
		CallExpr expr = (CallExpr)stmt.getCmd();
		String cmd = expr.readArg(0);
		if(isBlank(cmd))
			return null;
		
		if (streql(expr.readArg(0), "[") && streql(expr.readArg(-1), "]")) {
			return new Exec(stmt, CmdType.ShellOp, null, null);
		}

		//get func, if any
		Func func = funcTable.get(cmd);
		if (func != null) {
			return new Exec(stmt, CmdType.Func, func.getShpath(), func);  //TODO remote duplicated dependency
		}
		else if(equalAny(cmd, ".", "source")){
			String cmdfileStr = expr.readArg(1);

			Pair<CmdType, FsPath> result = inferFsPath(cmdfileStr, false);
			if(result == null || result.getRight().isDirectory())
				return null;

			return new Exec(stmt, CmdType.Builtin, result.getRight(), null);
		}
		else if (builtinCmds.contains(cmd)) {
			return new Exec(stmt, CmdType.Builtin, null, null);
		}
		else{
			Pair<CmdType, FsPath> result = inferFsPath(cmd, true);
			if(result == null || result.getRight().isDirectory())
				return null;

			return new Exec(stmt, result.getLeft(), result.getRight(), null);
		}
	}

	// helpers =========================================
	private FsPath getHomeDir(){
		try{
			Path userfile = fsroot.resolve("etc/passwd");
			if(Files.exists(userfile)){
				Path home = Commands.getHomeDir(userfile, DEF_USR);  //assume root
				if(home != null){
					logger.info("get {}'s home: {}", DEF_USR, home);
					return FsPath.of(fsroot, home);
				}
			}
		}
		catch (Exception e) {
			logger.error("get {}'s home error: {}", DEF_USR, getStackTrace(e));
		}

		logger.info("not get {}'s home, set default: {}", DEF_USR, "/root");
		return FsPath.of(fsroot, "/root");  //using usual home as default
	}

	
	//TODO VERY important! to check if a file is shell script or not
	private static boolean isShellScript(Path f) {
		try {
			String type = Commands.fileMagic(f.toFile());
			boolean isScript = (type.contains("script") && type.contains("executable")) ||
								(type.contains("ASCII") && f.toString().endsWith(".sh"));
			
			logger.info("The file '{}' {} shell script", f, isScript? "is": "is NOT");
			return isScript;
		} catch (IOException | InterruptedException e) {
			logger.error("detect if '{}' is shell script error: {}", f, getStackTrace(e));
			return false;
		}
	}
	
	private boolean isAbsPath(String path){
		return path.startsWith("/");
	}

	private FsPath getCwd() {
		return FsPath.of(fsroot, shellVar.get(CWD));
	}

	// detect absolute or relative path, then return file
	private Pair<CmdType,FsPath> inferFsPath(String path, boolean usingEnvPath) {
		if (isBlank(path))
			return null;

		else if (path.startsWith("/"))
			return Pair.of(CmdType.AbsPath, FsPath.of(fsroot, path));

		else if (path.contains("/") || !usingEnvPath)
			return Pair.of(CmdType.AbsPath, getCwd().append(path));

		else{
			FsPath fspath = prefixEnvPath(path);
			//TODO this is a trade-off.
			// we 'partially' known where is the cmd, but cannot sure.
			// So we put a 'pseudo fspath'. Any more improve?
			if(fspath == null)
				fspath = FsPath.of(fsroot, "$PATH/" + path);
			return Pair.of(CmdType.EnvPath, fspath);
		}
	}

	// search @exec file under PATH, like 'which' cmd
	private FsPath prefixEnvPath(String cmd) {
		for (FsPath path : envPath) {
			FsPath execFile = path.append(cmd);
			if (execFile.exists())
				return execFile;
		}
		return null;
	}
	// handle builtin commands ==========================
	/*
	 * private ScriptFile source(String path){ File src = toFile(path); try {
	 * if(src != null && src.isFile() && !scripts.containsKey(src)){
	 * logger.info("source script file '{}'", src.getAbsolutePath()); return
	 * load(src); } } catch (Exception e) {
	 * logger.error("source script file '{}' error: {}", src.getAbsolutePath(),
	 * getStackTrace(e)); }
	 * 
	 * return null; }
	 */

}
