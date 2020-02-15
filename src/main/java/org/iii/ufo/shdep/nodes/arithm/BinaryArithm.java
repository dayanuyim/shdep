package org.iii.ufo.shdep.nodes.arithm;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.json.JSONObject;

public class BinaryArithm implements ArithmExpr{
	private final Op op;
	private final ArithmExpr x;
	private final ArithmExpr y;

	public BinaryArithm(JSONObject obj){
		op = Op.from(obj.getInt("Op"));
		x = ArithmExpr.of(obj.getJSONObject("X"));
		y = ArithmExpr.of(obj.getJSONObject("Y"));
	}

	public Op getOp() {
		return op;
	}

	public ArithmExpr getX() {
		return x;
	}

	public ArithmExpr getY() {
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
