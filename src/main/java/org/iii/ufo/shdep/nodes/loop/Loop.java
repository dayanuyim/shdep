package org.iii.ufo.shdep.nodes.loop;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loop extends Node{
	public static final Logger logger = LoggerFactory.getLogger(Loop.class);

	//interface
	public void accept(NodeVisitor visitor);

	public static Loop of(JSONObject obj){
			String type = obj.getString("Type");
			switch (type) {
			case "WordIter": return new WordIter(obj);
			default:
				logger.error("Unsupport loop type: {}", type);
				return null;
			}
	}
}
