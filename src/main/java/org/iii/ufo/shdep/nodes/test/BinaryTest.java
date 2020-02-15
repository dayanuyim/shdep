package org.iii.ufo.shdep.nodes.test;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.json.JSONObject;

public class BinaryTest implements TestExpr{
	private final Op op;
	private final TestExpr x;
	private final TestExpr y;

	public BinaryTest(JSONObject obj){
		op = Op.from(obj.getInt("Op"));
		x = TestExpr.of(obj.getJSONObject("X"));
		y = TestExpr.of(obj.getJSONObject("Y"));
	}

	public Op getOp() {
		return op;
	}

	public TestExpr getX() {
		return x;
	}

	public TestExpr getY() {
		return y;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString(){
		return String.format("%s %s %s", x, op.lit(), y);
	}
	

}
