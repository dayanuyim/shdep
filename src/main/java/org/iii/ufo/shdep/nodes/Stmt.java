package org.iii.ufo.shdep.nodes;

import java.util.List;
import java.util.stream.Collectors;

import org.iii.ufo.shdep.nodes.cmd.Command;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.iii.utils.CommonUtils.*;

/*
 * Expecting "Cmd" field, but has exception.
 * The command without 'Cmd' field:
 * 		/usr/sbin/p2pcam_pan-tilt -v& >> ${log_path} 2>&1
 */
public class Stmt implements Node{
	private static final Logger logger = LoggerFactory.getLogger(Stmt.class);
	private final Command cmd;
	private final boolean negated;
	private final boolean background;
	private final boolean coprocess;
	private final List<Redirect> redirs;

	public Stmt(JSONObject obj){
		cmd = Node.toCommand(obj.optJSONObject("Cmd"));  //may only contain redirects. Why?
		negated = obj.optBoolean("Negated");
		background = obj.optBoolean("Background");
		coprocess = obj.optBoolean("Coprocess");
		redirs = tolist(obj.optJSONArray("Redirs")).stream()
				.map(Redirect::new)
				.collect(Collectors.toList());
	}

	public Command getCmd() {
		return cmd;
	}
	
	public static Logger getLogger() {
		return logger;
	}

	public boolean isNegated() {
		return negated;
	}

	public boolean isBackground() {
		return background;
	}

	public boolean isCoprocess() {
		return coprocess;
	}

	public List<Redirect> getRedirs() {
		return redirs;
	}

	@Override
	public void accept(NodeVisitor visitor){
		visitor.visit(this);
	}

	
	@Override
	public String toString(){
		String result = "";

		if(negated) result += "! ";

		if(cmd != null)
			result += cmd.toString();
		
		for (Redirect redir : redirs)
			result += " " + redir.toString();

		if(background) result += " &";

		if(coprocess) result += " |&"; //mksh's co-process

		return result;
	}

}
