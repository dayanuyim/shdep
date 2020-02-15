package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.arithm.ArithmExpr;
import org.json.JSONObject;

public class ArithmExp implements WordPart{
	
	private final boolean bracket;
	private final boolean unsigned;
	private final ArithmExpr x;

	public ArithmExp(JSONObject obj){
		bracket = obj.optBoolean("Bracket");
		unsigned = obj.optBoolean("Unsigned");
		x = ArithmExpr.of(obj.getJSONObject("X"));
	}
	
	public boolean isBracket() {
		return bracket;
	}

	public boolean isUnsigned() {
		return unsigned;
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
		if(bracket)
			return "$[" + x.toString() + "]";
		else if(unsigned)
			return "$((# " + x.toString() + "]";
		else
			return "$((" + x.toString() + "))";
	}
}
