package org.iii.ufo.shdep.nodes.cmd;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Command extends Node{
	public static final Logger logger = LoggerFactory.getLogger(Command.class);
	public static final String INDENT = "    "; //4 spaces

	//interface
	public void accept(NodeVisitor visitor);

	public static Command of(JSONObject obj){
		String type = obj.getString("Type");
		//logger.info("Command[{}]", type);

		switch(type){
		case "CallExpr": return new CallExpr(obj);
		case "BinaryCmd": return new BinaryCmd(obj);
		case "FuncDecl": return new FuncDecl(obj);
		case "Block": return new Block(obj);
		case "IfClause": return new IfClause(obj);
		case "CaseClause": return new CaseClause(obj);
		case "WhileClause": return new WhileClause(obj);
		case "DeclClause": return new DeclClause(obj);
		case "Subshell": return new Subshell(obj);
		case "ForClause": return new ForClause(obj);
		case "LetClause": return new LetClause(obj);
		case "TestClause": return new TestClause(obj);
/*
func (*ArithmCmd) commandNode()    {}                                                                                                                           
func (*TimeClause) commandNode()   {}                                                                                                                           
func (*CoprocClause) commandNode() {} 
*/
		default:
			logger.error("unsupport command type: {}", type);
			return null;
		}
	}

}
