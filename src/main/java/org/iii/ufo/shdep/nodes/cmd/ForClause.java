package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.StmtList;
import org.iii.ufo.shdep.nodes.loop.Loop;
import org.json.JSONObject;

public class ForClause implements Command{
	private final Loop loop;
	private final StmtList do_;
	
	public ForClause(JSONObject obj) {
		this.loop = Loop.of(obj.getJSONObject("Loop"));
		this.do_ =  new StmtList(obj.getJSONObject("Do"));
		// TODO Auto-generated constructor stub
	}
	
	public Loop getLoop() {
		return loop;
	}

	public StmtList getDo() {
		return do_;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	//ref whileClause
	public String toString(){
		String doBlock = INDENT + do_.toString("\n")
							.replaceAll("\n", "\n" + INDENT);
		
		return String.format("for %s\ndo\n%s\ndone", loop, doBlock);
	}
}

