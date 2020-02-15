package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.StmtList;
import org.json.JSONObject;

public class Subshell implements Command{
	private final StmtList stmtList;

	public Subshell(JSONObject obj){
		stmtList = new StmtList(obj.getJSONObject("StmtList"));
	}

	public StmtList getStmtList() {
		return stmtList;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString(){
		return "( " + stmtList.toString() + " )";
	}

}
