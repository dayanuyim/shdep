package org.iii.ufo.shdep.nodes.cmd;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.iii.ufo.shdep.nodes.StmtList;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.utils.CommonUtils;
import org.json.JSONObject;

public class CaseItem implements Node{
	
	private final Op op;
	private final List<Word> patterns;
	private final StmtList stmtList;

	public CaseItem(JSONObject obj){
		this.op = Op.from(obj.getInt("Op"));
		this.patterns = CommonUtils.tolist(obj.getJSONArray("Patterns"))
							.stream()
							.map(Word::new)
							.collect(Collectors.toList());
		this.stmtList = new StmtList(obj.getJSONObject("StmtList"));
	}
	
	public Op getOp() {
		return op;
	}

	public List<Word> getPatterns() {
		return patterns;
	}

	public StmtList getStmtList() {
		return stmtList;
	}

	@Override
	public String toString(){
		String stmts = stmtList.toString("\n" + Command.INDENT, Command.INDENT, "");
		String words = StringUtils.join(patterns, ")\n")  + ")";
		
		return String.format("%s\n%s\n%s%s",
					words, stmts, Command.INDENT, op.lit());
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
