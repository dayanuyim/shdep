package org.iii.ufo.shdep.nodes;

import org.iii.ufo.shdep.nodes.parts.Lit;
import org.json.JSONObject;

//please ref nodes.go: type Assign struct
public class Assign implements Node{
	private final boolean append;
	private final boolean naked;
	private final Lit name;
	private final Word value;
	//private final ArithmeticExpr index;
	//private final ArrayExpr array;
	
	public Assign(JSONObject obj){
		this.append = obj.optBoolean("Append");
		this.naked = obj.optBoolean("Naked");
		this.name = Node.toLit(obj.optJSONObject("Name"));
		this.value = Node.toWordOrEmpty(obj.optJSONObject("Value"));
	}
	

	public boolean isAppend() {
		return append;
	}

	public boolean isNaked() {
		return naked;
	}

	public Lit getName() {
		return name;
	}

	public Word getValue() {
		return value;
	}
	
	@Override
	public String toString(){
		//if naked, only value (but may put in name or value)
		if(naked){
			return name != null? name.toString(): value.toString();
		}
		else if(append){
			return String.format("%s+=%s", name, value);
		}
		else
			return String.format("%s=%s", name, value);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
