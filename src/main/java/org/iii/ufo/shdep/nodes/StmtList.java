package org.iii.ufo.shdep.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import static org.iii.utils.CommonUtils.*;

public class StmtList implements Iterable<Stmt>, Node {
	
	private final List<Stmt> stmts;
	//private final Stream<String> comments;

	public StmtList(JSONObject obj){
		stmts = tolist(obj.optJSONArray("Stmts")).stream()  //maybe empty
				.map(Stmt::new)
				.collect(Collectors.toList());
	}
	
	@Override
	public Iterator<Stmt> iterator() {
		return stmts.iterator();
	}
	
	public Stream<Stmt> stream(){
		return stmts.stream();
	}
	
	public boolean isEmpty(){
		return stmts.isEmpty();
	}
	
	public Stmt get(int idx){
		return stmts.get(idx);
	}

	@Override
	public String toString(){
		return toString("; ");
	}
	
	public String toString(String sep){
		return toString(sep, "", "");
	}
	
	public String toString(String sep, String prefix, String suffix){
		return stmts.stream()
				.map(Stmt::toString)
				.collect(Collectors.joining(sep, prefix, suffix));
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
