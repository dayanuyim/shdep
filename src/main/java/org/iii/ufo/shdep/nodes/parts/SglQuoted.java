package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.json.JSONObject;

public class SglQuoted implements WordPart {
	
	private final String value;
	private final boolean dollar;

	public SglQuoted(JSONObject obj){
		this.value = obj.optString("Value"); //may empty string
		this.dollar = obj.optBoolean("Dollar");
	}

	public String getValue() {
		return value;
	}

	public boolean isDollar() {
		return dollar;
	}

	@Override
	public String toString() {
		return "'" + value + "'";
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
