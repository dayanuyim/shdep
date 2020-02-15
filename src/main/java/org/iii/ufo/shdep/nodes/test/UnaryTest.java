package org.iii.ufo.shdep.nodes.test;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.json.JSONObject;

public class UnaryTest implements TestExpr{
	private final Op op;
	private final TestExpr x;
	
	public UnaryTest(JSONObject obj) {
		op = Op.from(obj.getInt("Op"));
		x = TestExpr.of(obj.getJSONObject("X"));
	}

	public Op getOp() {
		return op;
	}

	public TestExpr getX() {
		return x;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	// TODO: detect post or pre @op.
	// UnaryTest represents an unary expression over a node, either before or after it.
	@Override
	public String toString(){
		return String.format("%s%s", op.lit(), x.toString());
	}
}
