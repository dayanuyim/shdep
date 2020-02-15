package org.iii.ufo.shdep.nodes;

import java.util.Collections;

import org.iii.ufo.shdep.nodes.arithm.ArithmExpr;
import org.iii.ufo.shdep.nodes.cmd.Command;
import org.iii.ufo.shdep.nodes.parts.Lit;
import org.json.JSONObject;

public interface Node {
	public void accept(NodeVisitor visitor);
	
	
	//utils =======================================
	public static Lit toLit(JSONObject obj){
		return (obj == null)? null: new Lit(obj);
	}

	public static Word toWord(JSONObject obj){
		return (obj == null)? null: new Word(obj);
	}
	
	public static Word toWordOrEmpty(JSONObject obj){
		return (obj == null)? 
			new Word(Collections.emptyList()):
			new Word(obj);
	}

	
	public static ArithmExpr toArithmExpr(JSONObject obj){
		return (obj == null)? null: ArithmExpr.of(obj);
	}

	public static Command toCommand(JSONObject obj){
		return (obj == null)? null: Command.of(obj);
	}
}
