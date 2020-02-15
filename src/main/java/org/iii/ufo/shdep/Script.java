package org.iii.ufo.shdep;

import java.io.IOException;

import org.iii.ufo.shdep.nodes.ScriptFile;
import org.iii.ufo.utils.Commands;

public class Script {
	//private final Exec exec;          // Command: The wapper of Callexpr
	private final FsPath path;
	private final ScriptFile ast; // Content: AST for script file

	// Although @file usually euqlas @exec.file, e.g., ./some.sh, but
	// can be diffrent, e.g., source some.sh
	public Script(FsPath file) throws IOException, InterruptedException{
		//this.exec = exec; 
		this.path = file;
		this.ast = new ScriptFile(Commands.shfmt(file.getFullPath(), true));
	}

	public FsPath getPath() {
		return path;
	}

	/*
	public Exec getExec() {
		return exec;
	}
	*/

	public ScriptFile getAST() {
		return ast;
	}
	
}