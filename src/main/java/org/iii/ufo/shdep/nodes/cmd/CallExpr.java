package org.iii.ufo.shdep.nodes.cmd;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.iii.ufo.shdep.nodes.Assign;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.utils.CommonUtils;
import org.json.JSONObject;

import static org.iii.utils.CommonUtils.*;

public class CallExpr implements Command{
	private final List<Assign> assigns;
	private final List<Word> args;
	
	public CallExpr(JSONObject obj){
		assigns = tolist(obj.optJSONArray("Assigns")).stream()
					.map(Assign::new)
					.collect(Collectors.toList());

		args = tolist(obj.optJSONArray("Args")).stream()
					.map(Word::new)
					.collect(Collectors.toList());
	}
	
	public CallExpr(List<Assign> assigns, List<Word> args){
		this.assigns = assigns;
		this.args = args;
	}

	public List<Word> getArgs(){
		return args;
	}

	public List<Assign> getAssigns() {
		return assigns;
	}
	
	@Override
	public void accept(NodeVisitor visitor){
		visitor.visit(this);
	}

	public String readAssign(int idx){
		Assign assign = CommonUtils.elementAt(assigns, idx);
		return assign == null? null: assign.toString();
	}

	//return Empty String if no exist
	public String readArg(int idx){
		Word arg = CommonUtils.elementAt(args, idx);
		return arg == null? null: arg.toString();
	}
	
	@Override
	public String toString(){
		return Stream.concat(
					assigns.stream().map(Assign::toString),
					args.stream().map(Word::toString))
				.collect(Collectors.joining(" "));
	}
	
}
