package org.iii.ufo.shdep;

import org.apache.commons.lang3.StringUtils;
import org.iii.utils.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.iii.utils.CommonUtils.equalAny;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.iii.utils.CommonUtils.*;

public class ScriptDep {
	public static class ScriptCall{
		private final FsPath path;
		private final List<String> args;

		public ScriptCall(FsPath path, List<String> args){
			this.path = path;
			this.args = args;
		}

		public FsPath getPath() { return path; }
		public List<String> getArgs() { return args; }
		
		@Override
		public boolean equals(Object obj){
			if(!(obj instanceof ScriptCall))
				return false;
			ScriptCall that = (ScriptCall) obj;
			return Objects.equals(this.path, that.path) &&
					Objects.equals(this.args, that.args);
		}
		
		@Override
		public int hashCode(){
			return Objects.hash(this.path, this.args);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ScriptDep.class);

	public static void main(String[] args){
		try{
			if(isEmpty(args)){
				logger.error("usage: ScriptDep <fsroot> [target-script]");
				return;
			}

			Path fsroot = Paths.get(args[0]);
			String target = elementAt(args, 1);

			//analysis
			ScriptDep dep = new ScriptDep(fsroot);
			if(target != null)
				dep.analysis(target);
			else
				dep.analysisFS();
			
			//print result
			try(PrintWriter writer = new PrintWriter("/tmp/shdep")){
				dep.walkScriptDep(paths->{ 
					try {
						String line = paths.stream()
									.map(t->t.getInnerPath().toString())
									.collect(Collectors.joining(" -> "));

						writer.println(line);
						System.out.println(line);
					}
					catch (Exception e) {
						logger.error("write shdep error: {}", getStackTrace(e));
					}
				});
			}
		}
		catch (Exception e) {
			logger.error("ScriptDep error: {}", getStackTrace(e));
		}
	}

	private final Shell shell;
	private final Map<FsPath, Set<FsPath>> dependants = new HashMap<>();
	private final Set<FsPath> isolates = new HashSet<>();
	private final Set<ScriptCall> runnedScripts = new HashSet<>();  //for dep cycling
	
	public ScriptDep(Path fsroot) throws IOException, InterruptedException
	{
		//create shell
		this.shell = new Shell(fsroot);

		//rec runned script, avoid to run twice 
		this.shell.setHook((script, args_)->{
			ScriptCall sc = new ScriptCall(script.getPath(), args_);
			boolean firstRun = runnedScripts.add(sc);

			logger.info("Script run: [{}] script='{}', args='{}'", firstRun? "O": "X", script.getPath().getInnerPath(), args_);
			return firstRun;
		});
	}
	
	//single script
	public void analysis(String path) throws IOException, InterruptedException{
		final Path fsroot = shell.getFsroot();
		FsPath fspath = FsPath.of(fsroot, path);

		//print script
		Script script = new Script(fspath);
		System.out.println("======= Script Begin =======");
		System.out.println(script.getAST());
		System.out.println("======== Script End ========");

		//run
		Exec exec = shell.run("'" + fspath.getInnerPath() + "'");
		printExec(exec);
		
		//script dep
		buildScriptDep(exec);
		
		// is isolated?
		if(isEmpty(dependants.get(exec.getCmdpath())))
			isolates.add(fspath);
		
		logAnalaysis();
	}
	
	// all scripts under @fsroot
	public void analysisFS() throws IOException, InterruptedException{
		final Path fsroot = shell.getFsroot();

		// Becareful: script may not run due to Shell-Dependency-Cycling Detection
		List<Path> paths = findAllScripts(fsroot);
		logger.info("find initial {} scripts under {}", paths.size(), fsroot);

		int sn = 0;
		for(Path path: paths){
			FsPath fspath = FsPath.fromFull(fsroot, path);

			//run
			logger.info("run sript({}): '{}'", ++sn, fspath.getInnerPath());
			Exec exec = shell.run("'" + fspath.getInnerPath() + "'");
			//printExec(exec);

			//script dep
			buildScriptDep(exec);

			//is isolated?
			if(isEmpty(dependants.get(exec.getCmdpath()))){
				//logger.info("isolated script: {}", fspath.getInnerPath());
				isolates.add(fspath);
			}
			
			logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}

		logAnalaysis();
	}
	
	//TODO analysis(List<FsPath> files)
	
	private void logAnalaysis(){
		logger.info("find {} isolated script, {} dependent scripts", isolates.size(), dependants.size());
		for(FsPath isolate: isolates)
			logger.info("isolated script: {}", isolate.getInnerPath());
	}

	private static List<Path> findAllScripts(Path path) throws IOException, InterruptedException{
		String cmd = String.format("{ find '%s' -type f -name '*.sh' & find '%s' -type f -exec file {} \\; | grep -i 'shell script' | cut -d':' -f1; } | sort -u ",
					path, path);
		logger.info(cmd);

		String[] lines = Command.cmd(cmd).check().run().getStdout().split("\n");
		return Stream.of(lines)
				.filter(StringUtils::isNoneEmpty)
				.map(Paths::get)
				.collect(Collectors.toList());
	}
	
	// print script dep =================================================================
	
	public void walkScriptDep(ScriptDepAction action){

		//duplicate, cause dep will be altered
		//Map<FsPath,Set<FsPath>> dep = new HashMap<>(this.dependants);

		/*
		//debug print
		dep.forEach((parent, children)->{
			System.out.println(String.format("%s -> %s",
					parent.getInnerPath(), 
					children.stream()
						.map(child->child.getInnerPath().toString())
						.collect(Collectors.toList())));
		});
		*/
		
		//bookkeeper for cycling
		Set<FsPath> untoucheds = new HashSet<>(dependants.keySet());
		
		//get 'starters', which have no parent; otherwise, 'followers'
		Set<FsPath> starters = new HashSet<>(dependants.keySet());
		dependants.forEach((parent, children)->{
			children.forEach(child->starters.remove(child));
		});

		logger.info("There are {} isolates, {} dependants({} starters, {} followers)",
				isolates.size(), dependants.size(), starters.size(), dependants.size() - starters.size());
		
		//walk for isolates
		for(FsPath isolate: isolates)
			action.act(Arrays.asList(isolate));

		//walk from starters
		for(FsPath starter: starters){
			List<FsPath> ancesotrs = new ArrayList<>();
			_walkScriptDep(untoucheds, ancesotrs, starter, action);
		}
		
		//walk for cycling 
		while(!untoucheds.isEmpty()){
			logger.warn("found script dependency cycle!!!");
			FsPath starter = untoucheds.iterator().next();  //get anyone as starter
			List<FsPath> ancesotrs = new ArrayList<>();
			_walkScriptDep(untoucheds, ancesotrs, starter, action);
		}
	}
	
	// @untoucheds: record for cycleing detection
	private void _walkScriptDep(Set<FsPath> untocheds, List<FsPath> ancestors, FsPath folk, ScriptDepAction action)
	{
		untocheds.remove(folk);
		boolean hasCycle = ancestors.contains(folk);

		ancestors.add(folk);
		try{
			Set<FsPath> children = null;

			//The End
			if(hasCycle || (children = dependants.get(folk)) == null){
				action.act(ancestors);
				return;
			}

			//recursive
			for(FsPath child: children)
				_walkScriptDep(untocheds, ancestors, child, action);
		}
		finally {
			ancestors.remove(ancestors.size() -1);
		}
	}


	public void buildScriptDep(Exec exec){

		if(exec.getCmdpath() == null)  //ref: Shell::toExec(), ShellOp or Buitin(but 'source')
			return;
		
		for(Exec child: exec.getChildren()){
			if(child.getCmdpath() == null)  //ref: Shell::toExec(), ShellOp or Buitin(but 'source')
				continue;

			//add to table
			if(!exec.getCmdpath().equals(child.getCmdpath())){
					
				Set<FsPath> files = dependants.get(exec.getCmdpath());
				if(files == null){
					files = new HashSet<>();
					dependants.put(exec.getCmdpath(), files);
				}
				files.add(child.getCmdpath());
			}
			
			//recursive
			buildScriptDep(child);
		}
	}
	
	// print Exec Path ===========================================
	
	private static void printExec(Exec exec){
		_printExec(exec, 0);
	}

	private static void _printExec(Exec exec, int level){
		
		String fileinfo = equalAny(exec.getType(), CmdType.Builtin, CmdType.ShellOp)? "":
							(exec.getCmdpath() == null)? " (NULL)":
							String.format(" (%s)", exec.getCmdpath().getInnerPath());

		//TODO test @@!
		//fileinfo = "";

		System.out.println(String.format("%s<%s> %s%s",
				StringUtils.repeat(' ', 4*level), exec.getType(), exec, fileinfo));
		
		for(Exec e: exec.getChildren())
			_printExec(e, level + 1);
	}
}
