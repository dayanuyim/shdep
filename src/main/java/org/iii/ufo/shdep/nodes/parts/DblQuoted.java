package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Word;
import org.json.JSONObject;

public class DblQuoted implements WordPart{

	private final Word word;
	private final boolean dollar;

	public DblQuoted(JSONObject obj){
		this.word = new Word(obj);
		this.dollar = obj.optBoolean("Dollar");
	}

	public Word getWord() {
		return word;
	}

	public boolean isDollar() {
		return dollar;
	}

	@Override
	public String toString() {
		return "\"" + word.toString() + "\"";
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
