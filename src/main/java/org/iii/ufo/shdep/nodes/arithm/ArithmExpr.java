package org.iii.ufo.shdep.nodes.arithm;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Word;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ArithmExpr extends Node{
	public static Logger logger = LoggerFactory.getLogger(ArithmExpr.class);

	public void accept(NodeVisitor visitor);

	//TODO evaluate expressions
	public static ArithmExpr of(JSONObject obj){
		String type = obj.getString("Type");
		switch (type) {
		case "BinaryArithm": return new BinaryArithm(obj);
		case "UnaryArithm": return new UnaryArithm(obj);
		case "ParenArithm": return new ParenArithm(obj);
		case "Word": return new Word(obj);
		default:
			logger.error("unsupport ArithmExpr[{}]", type);
			return null;
		}
	}

}
