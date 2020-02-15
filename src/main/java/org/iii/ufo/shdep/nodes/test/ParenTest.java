package org.iii.ufo.shdep.nodes.test;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.json.JSONObject;

public class ParenTest implements TestExpr{

	private final TestExpr x;
	
	public ParenTest(JSONObject obj) {
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
		return String.format("((%s))", x);
	}

}
