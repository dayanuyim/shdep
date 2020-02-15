package org.iii.ufo.shdep.nodes.loop;

import java.util.List;
import java.util.stream.Collectors;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.ufo.shdep.nodes.parts.Lit;
import org.iii.utils.CommonUtils;
import org.json.JSONObject;

public class WordIter implements Loop{
	
	private final Lit name;
	private final List<Word> items; 

	public WordIter(JSONObject obj){
		this.name = Node.toLit(obj.optJSONObject("Name"));
		this.items = CommonUtils.tolist(obj.optJSONArray("Items")).stream()
						.map(Word::new)
						.collect(Collectors.toList());
	}

	public Lit getName() {
		return name;
	}

	public List<Word> getItems() {
		return items;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString(){
		return String.format("for %s in %s", name, 
					items.stream()
					.map(Word::toString)
					.collect(Collectors.joining(" ")));
	}
	

}
