package org.iii.ufo.shdep.nodes.cmd;

import java.util.List;
import java.util.stream.Collectors;

import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.arithm.ArithmExpr;
import org.iii.utils.CommonUtils;
import org.json.JSONObject;

public class LetClause implements Command{
	
	private final List<ArithmExpr> exprs;
	
	public LetClause(JSONObject obj) {
		exprs = CommonUtils.tolist(obj.getJSONArray("Exprs")).stream()
					.map(ArithmExpr::of)
					.collect(Collectors.toList());
	}

	
	public List<ArithmExpr> getExprs() {
		return exprs;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString(){
		return "let " + exprs.stream()
			.map(ArithmExpr::toString)
			.collect(Collectors.joining(" "));
	}
}
