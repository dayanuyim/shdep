package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface WordPart extends Node{
	public static final Logger logger = LoggerFactory.getLogger(WordPart.class);

	//interface
	public void accept(NodeVisitor visitor);

	public static WordPart of(JSONObject obj){
		String type = obj.getString("Type");
		switch(type){
		case "Lit":       return new Lit(obj);
		case "SglQuoted": return new SglQuoted(obj);
		case "ParamExp":  return new ParamExp(obj);
		case "DblQuoted": return new DblQuoted(obj);
		case "CmdSubst": return new CmdSubst(obj);
		case "ArithmExp": return new ArithmExp(obj);
		case "ExtGlob": return new ExtGlob(obj);
		/*
		case "ProcSubst": return null;
		*/
		default:
			logger.error("Unsupport part type '{}'", type);
			return null;
		}
	}
}
