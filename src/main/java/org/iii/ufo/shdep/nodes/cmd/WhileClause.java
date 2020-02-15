package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.StmtList;
import org.json.JSONObject;

public class WhileClause implements Command{

	private final boolean until;
	private final StmtList cond;
	private final StmtList do_;

	public WhileClause(JSONObject obj){
		this.until = obj.optBoolean("Until");
		this.cond = new StmtList(obj.getJSONObject("Cond"));
		this.do_ = new StmtList(obj.getJSONObject("Do"));
	}
	
	public boolean isUntil() {
		return until;
	}

	public StmtList getCond() {
		return cond;
	}

	public StmtList getDo() {
		return do_;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString(){
		//String doBlock = do_.toString("\n" + INDENT, Command.INDENT, "");
		String doBlock = INDENT + do_.toString("\n")
							.replaceAll("\n", "\n" + INDENT);
		return String.format("%s %s\ndo\n%s\ndone",
				until? "until": "while", cond, doBlock);
	}

}
