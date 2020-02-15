package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.iii.ufo.shdep.nodes.Stmt;
import org.json.JSONObject;

public class BinaryCmd implements Command{
	private final Op op;
	private final Stmt x;
	private final Stmt y;
	
	public BinaryCmd(JSONObject obj){
		this.op = Op.from(obj.getInt("Op"));
		this.x = new Stmt(obj.getJSONObject("X"));
		this.y = new Stmt(obj.getJSONObject("Y"));
	}

	public Op getOp() {
		return op;
	}

	public Stmt getX() {
		return x;
	}

	public Stmt getY() {
		return y;
	}
	
	@Override
	public void accept(NodeVisitor visitor){
		visitor.visit(this);
	}

	@Override
	public String toString(){
		return String.format("%s %s %s", x, op.lit(), y);
	}
}
