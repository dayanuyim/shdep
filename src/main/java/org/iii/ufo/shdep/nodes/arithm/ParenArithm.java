package org.iii.ufo.shdep.nodes.arithm;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.json.JSONObject;

public class ParenArithm implements ArithmExpr{

	private final ArithmExpr x;
	
	public ParenArithm(JSONObject obj) {
		x = ArithmExpr.of(obj.getJSONObject("X"));
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
		return String.format("(%s)", x);
	}

}
