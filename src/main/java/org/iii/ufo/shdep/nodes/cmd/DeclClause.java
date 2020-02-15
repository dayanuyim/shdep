package org.iii.ufo.shdep.nodes.cmd;

import java.util.List;
import java.util.stream.Collectors;

import org.iii.ufo.shdep.nodes.Assign;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.ufo.shdep.nodes.parts.Lit;
import org.iii.utils.CommonUtils;
import org.json.JSONObject;

public class DeclClause implements Command{
	private final Lit variant;
	private final List<Word> opts;
	private final List<Assign> assigns;
	
	public DeclClause(JSONObject obj){
		variant = new Lit(obj.getJSONObject("Variant"));

		opts = CommonUtils.tolist(obj.optJSONArray("Opts")).stream()
				.map(Word::new)
				.collect(Collectors.toList());

		assigns = CommonUtils.tolist(obj.optJSONArray("Assigns")).stream()
				.map(Assign::new)
				.collect(Collectors.toList());
	}

	public Lit getVariant() {
		return variant;
	}


	public List<Word> getOpts() {
		return opts;
	}


	public List<Assign> getAssigns() {
		return assigns;
	}


	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString(){
		String result = variant.toString();
		for(Word opt: opts)
			result += " " + opt.toString(); 
		for(Assign assign: assigns)
			result += " " + assign.toString();
		return result;
	}
}
