package org.iii.ufo.shdep;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iii.ufo.shdep.nodes.Stmt;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.ufo.shdep.nodes.cmd.CallExpr;


//Stmt wrapper, assume Stmt's cmd is CallExpr type, for recording exec
public class Exec{
	private final Stmt stmt;   	 //cmd + args
	private final CmdType type;     //cmd typ
	private final FsPath cmdpath;   //cmd file,  if reference to a file
	private final Func func;   //func, if reference to a function
	private final List<Exec> children = new ArrayList<>();

	public Stmt getStmt() { return stmt; }
	public CmdType getType() { return type; }
	public FsPath getCmdpath() { return cmdpath; }
	public Func getFunc() { return func; }
	public List<Exec> getChildren() { return children; }

	public CallExpr getExpr() { return (CallExpr)stmt.getCmd(); }
	public Path getAbsPath() { return (cmdpath == null)? null: cmdpath.getFullPath(); }

	/*
		public Exec(Script script){
			this.node = script.getScript();
			this.type = CmdType.AbsPath;
			this.file = script.getFile();
			this.func = null;
		}
	 */

	public Exec(Stmt stmt, CmdType type, FsPath fspath, Func func){
		this.stmt = stmt;
		this.type = type;
		this.cmdpath = fspath;
		this.func = func;
	}

	public void addChild(Exec exec){
		children.add(exec);
	}

	public void addChildren(Collection<Exec> execs){
		children.addAll(execs);
	}

	public String getCmd(){
		return getExpr().readArg(0).toString();
	}
	
	public List<Word> getArgs(){
		return getExpr().getArgs();
	}

	public String readArg(int idx){
		return getExpr().readArg(idx);
	}

	@Override
	public String toString(){
		return stmt.toString();
	}
}
