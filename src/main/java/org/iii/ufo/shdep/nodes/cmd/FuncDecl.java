package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Stmt;
import org.iii.ufo.shdep.nodes.parts.Lit;
import org.json.JSONObject;

public class FuncDecl implements Command{

	private final Lit name;
	private final Stmt body;
	private final boolean rsrvWord;
	
	public FuncDecl(JSONObject obj){
		this.name = new Lit(obj.getJSONObject("Name"));
		this.body = new Stmt(obj.getJSONObject("Body"));
		this.rsrvWord = obj.optBoolean("RsrvWord");
	}

	public Lit getName() {
		return name;
	}

	public Stmt getBody() {
		return body;
	}
	
	@Override
	public void accept(NodeVisitor visitor){
		visitor.visit(this);
	}

	@Override
	public String toString(){
		return String.format("%s%s() %s", (rsrvWord? "function ": ""), name, body);

	}
}
