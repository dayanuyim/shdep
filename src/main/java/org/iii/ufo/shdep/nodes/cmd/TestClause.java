package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.test.TestExpr;
import org.json.JSONObject;

public class TestClause implements Command {
	
	private final TestExpr x;
	
	public TestClause(JSONObject obj){
		x = TestExpr.of(obj.getJSONObject("X"));
	}

	public TestExpr getX() {
		return x;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString(){
		return String.format("[[ %s ]]", x);
	}
}
