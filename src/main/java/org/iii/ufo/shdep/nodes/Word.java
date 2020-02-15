package org.iii.ufo.shdep.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.iii.ufo.shdep.nodes.arithm.ArithmExpr;
import org.iii.ufo.shdep.nodes.parts.WordPart;
import org.iii.ufo.shdep.nodes.test.TestExpr;
import org.json.JSONObject;

import static org.iii.utils.CommonUtils.*;

//expect 'Parts' JSONArray
public class Word  implements ArithmExpr, TestExpr, Iterable<WordPart> {
	private final List<WordPart> parts;
	
	public Word(JSONObject obj){
		parts = tolist(obj.optJSONArray("Parts")).stream()   //no word parts if empty string
					.map(part->WordPart.of(part))
					.collect(Collectors.toList());
	}
	
	public Word(List<WordPart> parts){
		this.parts = parts;
	}

	public List<WordPart> getParts() {
		return parts;
	}

	@Override
	public String toString(){
		return parts.stream()
				.map(WordPart::toString)
				.collect(Collectors.joining());
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Iterator<WordPart> iterator() {
		return parts.iterator();
	}
	
	public Stream<WordPart> stream(){
		return parts.stream();
	}
}
