package org.iii.ufo.shdep.nodes.parts;

import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.json.JSONObject;

// ExtGlob represents a Bash extended globbing expression. Note that                                                                                        
// these are parsed independently of whether shopt has been called or                                                                                       
// not.                                                                                                                                                     
//                                                                                                                                                          
// This node will only appear in LangBash and LangMirBSDKorn. 
public class ExtGlob implements WordPart{
	
	private final Op op;
	private final Lit pattern;
	
	public ExtGlob(JSONObject obj) {
		op = Op.from(obj.getInt("Op"));
		pattern = Node.toLit(obj.getJSONObject("Pattern"));
	}

	public Op getOp() {
		return op;
	}

	public Lit getPattern() {
		return pattern;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString(){
		//TODO the format is not sure.
		return String.format("%s(%s)", op.lit(), pattern);
	}
}
