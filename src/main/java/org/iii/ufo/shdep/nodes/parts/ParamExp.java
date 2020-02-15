package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.arithm.ArithmExpr;
import org.json.JSONObject;

public class ParamExp implements WordPart{
	
	private final Lit param;
	private final boolean short_;
	private final boolean excl;
	private final boolean length;
	private final boolean width;
	private final ArithmExpr index;
	//private final Slice slice;
	//private final Replace repl;
	//private final Expansion exp;

	public ParamExp(JSONObject obj) {
		this.param = Node.toLit(obj.getJSONObject("Param"));
		this.short_ = obj.optBoolean("Short");
		this.excl = obj.optBoolean("Excl");
		this.length = obj.optBoolean("Length");
		this.width = obj.optBoolean("Width");
		this.index = Node.toArithmExpr(obj.optJSONObject("Index"));
	}

	public Lit getParam() {
		return param;
	}

	public boolean isShort() {
		return short_;
	}

	public boolean isExcl() {
		return excl;
	}

	public boolean isLength() {
		return length;
	}

	public boolean isWidth() {
		return width;
	}

	public ArithmExpr getIndex() {
		return index;
	}

	@Override
	public String toString() {
		if(short_)
			return "$" + param.getValue();
		
		return String.format("${%s%s%s%s%s}",
				excl? "!": "",
				length? "#": "",
				width? "%": "",
				param.getValue(),
				index != null? "[" + index.toString() + "]": "");
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
