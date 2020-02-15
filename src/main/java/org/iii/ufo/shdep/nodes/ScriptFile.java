package org.iii.ufo.shdep.nodes;

import org.json.JSONObject;

//The class is exactly like 'Block' ?
public class ScriptFile implements Node{
	
	private final StmtList stmtList;

	public ScriptFile(JSONObject obj) {
		stmtList = new StmtList(obj.getJSONObject("StmtList"));
	}
	
	
	public StmtList getStmtList() {
		return stmtList;
	}

	//helper to forward visitor
	public void accept(NodeVisitor visitor){
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return stmtList.toString("\n");
	}

}
