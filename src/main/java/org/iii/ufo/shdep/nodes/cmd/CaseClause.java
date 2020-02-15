package org.iii.ufo.shdep.nodes.cmd;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.StrBuilder;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.utils.CommonUtils;
import org.json.JSONObject;

public class CaseClause implements Command{
	
	private final Word word;
	private final List<CaseItem> items;
	
	public CaseClause(JSONObject obj){
		this.word = new Word(obj.getJSONObject("Word"));
		this.items = CommonUtils.tolist(obj.getJSONArray("Items"))
						.stream()
						.map(CaseItem::new)
						.collect(Collectors.toList());
	}
	
	public Word getWord() {
		return word;
	}

	public List<CaseItem> getItems() {
		return items;
	}

	@Override
	public void accept(NodeVisitor visitor){
		visitor.visit(this);
	}

	@Override
	public String toString(){
		StrBuilder sout = new StrBuilder();

		sout.append("case ").append(word).append(" in\n");
		items.forEach(item-> sout.appendln(item));
		sout.append("esac");

		return sout.toString();
	}

}
