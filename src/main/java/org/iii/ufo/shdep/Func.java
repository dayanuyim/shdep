package org.iii.ufo.shdep;

import org.iii.ufo.shdep.nodes.cmd.FuncDecl;

public class Func {
	private final FsPath shpath;  	//where the func declared.
	private final FuncDecl decl;  //AST of the func
	private boolean touched;       // prevent from cycling

	public FsPath getShpath() { return shpath; }
	public FuncDecl getDecl() { return decl; }
	public void touch(){ touched = true;}
	public boolean isTouched(){ return touched;}

	public Func(FsPath shpath, FuncDecl decl){
		this.shpath = shpath;
		this.decl = decl;
		this.touched = false;
	}
}

