package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.json.JSONObject;

public class Lit implements WordPart{
	
	private final String value;

	public Lit(JSONObject obj){
		this.value = obj.optString("Value");  //vlaue may not eixist, for example, a paramExp without name, from bad or ill-parsing script 
	}

	public Lit(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}


	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
