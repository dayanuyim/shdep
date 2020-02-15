package org.iii.ufo.shdep.nodes.arithm;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.json.JSONObject;

public class UnaryArithm implements ArithmExpr{
	private final Op op;
	private final boolean post;
	private final ArithmExpr x;
	
	public UnaryArithm(JSONObject obj) {
		op = Op.from(obj.getInt("Op"));
		post = obj.optBoolean("Post");
		x = ArithmExpr.of(obj.getJSONObject("X"));
	}

	public Op getOp() {
		return op;
	}

	public boolean isPost() {
		return post;
	}

	public ArithmExpr getX() {
		return x;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString(){
		if(post)
			return x.toString() + op.lit();
		else
			return op.lit() + x.toString();
	}

}
