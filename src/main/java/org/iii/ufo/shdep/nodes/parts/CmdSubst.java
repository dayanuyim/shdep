package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.StmtList;
import org.json.JSONObject;

public class CmdSubst implements WordPart {
	
	private final StmtList stmts;

	public CmdSubst(JSONObject obj) {
		stmts = new StmtList(obj.getJSONObject("StmtList"));
	}

	public StmtList getStmts() {
		return stmts;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return stmts.toString("; ", "$(", ")");
	}

	public String toCmdString() {
		return stmts.toString("; ");
	}
}
