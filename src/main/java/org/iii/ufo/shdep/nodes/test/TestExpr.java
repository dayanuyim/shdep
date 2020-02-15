package org.iii.ufo.shdep.nodes.test;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Word;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TestExpr extends Node {
	public static Logger logger = LoggerFactory.getLogger(TestExpr.class);

	public void accept(NodeVisitor visitor);

	//TODO evaluate expressions
	public static TestExpr of(JSONObject obj){
		String type = obj.getString("Type");
		switch (type) {
		case "BinaryTest": return new BinaryTest(obj);
		case "UnaryTest": return new UnaryTest(obj);
		case "ParenTest": return new ParenTest(obj);
		case "Word": return new Word(obj);
		default:
			logger.error("unsupport ArithmExpr[{}]", type);
			return null;
		}
	}

}
