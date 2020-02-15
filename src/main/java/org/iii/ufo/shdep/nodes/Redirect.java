package org.iii.ufo.shdep.nodes;

import org.iii.ufo.shdep.nodes.parts.Lit;
import org.json.JSONObject;

public class Redirect implements Node{

	private final Op op;
	private final Lit n;   // n>
	private final Word word;  // n> word
	private final Word hdoc;  // here document body
	
	public Redirect(JSONObject obj) {
		this.op = Op.from(obj.getInt("Op"));
		this.n = Node.toLit(obj.optJSONObject("N"));
		this.word = Node.toWord(obj.optJSONObject("Word"));
		this.hdoc = Node.toWord(obj.optJSONObject("Hdoc"));
	}
	
	public Op getOp() {
		return op;
	}

	public Lit getN() {
		return n;
	}

	public Word getWord() {
		return word;
	}

	public Word getHdoc() {
		return hdoc;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString(){
		String result = "";

		if(n != null) result += n.toString();
		result += op.lit();
		if(word != null) result += " " + word.toString();
		if(hdoc != null) result += hdoc.toString();

		return result;
	}

}
