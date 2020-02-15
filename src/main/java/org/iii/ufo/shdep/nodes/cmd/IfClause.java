package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.StmtList;
import org.json.JSONObject;

public class IfClause implements Command{
	private final StmtList cond;
	private final StmtList then;
	private final StmtList else_;

	public IfClause(JSONObject obj){
		this.cond = new StmtList(obj.getJSONObject("Cond"));
		this.then = new StmtList(obj.getJSONObject("Then"));
		this.else_ = new StmtList(obj.getJSONObject("Else"));
	}

	public StmtList getCond() {
		return cond;
	}

	public StmtList getThen() {
		return then;
	}

	public StmtList getElse() {
		return else_;
	}
	
	@Override
	public void accept(NodeVisitor visitor){
		visitor.visit(this);
	}

	@Override
	public String toString(){

		String thenBlock = INDENT + then.toString("\n").replaceAll("\n", "\n" + INDENT);
		String elseBlock = INDENT + else_.toString("\n").replaceAll("\n", "\n" + INDENT);

		return String.format("if %s; then\n%s%s\nfi",
					cond.toString(),
					thenBlock,
					else_.isEmpty()? "": "\nelse\n" + elseBlock);
	}
}
