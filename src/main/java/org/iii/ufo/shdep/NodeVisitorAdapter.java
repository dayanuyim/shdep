package org.iii.ufo.shdep;

import org.iii.ufo.shdep.nodes.Assign;
import org.iii.ufo.shdep.nodes.Node;
import org.iii.ufo.shdep.nodes.NodeVisitor;
import org.iii.ufo.shdep.nodes.Op;
import org.iii.ufo.shdep.nodes.Redirect;
import org.iii.ufo.shdep.nodes.ScriptFile;
import org.iii.ufo.shdep.nodes.Stmt;
import org.iii.ufo.shdep.nodes.StmtList;
import org.iii.ufo.shdep.nodes.Word;
import org.iii.ufo.shdep.nodes.arithm.BinaryArithm;
import org.iii.ufo.shdep.nodes.arithm.ParenArithm;
import org.iii.ufo.shdep.nodes.arithm.UnaryArithm;
import org.iii.ufo.shdep.nodes.cmd.BinaryCmd;
import org.iii.ufo.shdep.nodes.cmd.Block;
import org.iii.ufo.shdep.nodes.cmd.CallExpr;
import org.iii.ufo.shdep.nodes.cmd.CaseClause;
import org.iii.ufo.shdep.nodes.cmd.CaseItem;
import org.iii.ufo.shdep.nodes.cmd.DeclClause;
import org.iii.ufo.shdep.nodes.cmd.ForClause;
import org.iii.ufo.shdep.nodes.cmd.FuncDecl;
import org.iii.ufo.shdep.nodes.cmd.IfClause;
import org.iii.ufo.shdep.nodes.cmd.LetClause;
import org.iii.ufo.shdep.nodes.cmd.Subshell;
import org.iii.ufo.shdep.nodes.cmd.TestClause;
import org.iii.ufo.shdep.nodes.cmd.WhileClause;
import org.iii.ufo.shdep.nodes.loop.WordIter;
import org.iii.ufo.shdep.nodes.parts.ArithmExp;
import org.iii.ufo.shdep.nodes.parts.CmdSubst;
import org.iii.ufo.shdep.nodes.parts.DblQuoted;
import org.iii.ufo.shdep.nodes.parts.ExtGlob;
import org.iii.ufo.shdep.nodes.parts.Lit;
import org.iii.ufo.shdep.nodes.parts.ParamExp;
import org.iii.ufo.shdep.nodes.parts.SglQuoted;
import org.iii.ufo.shdep.nodes.test.BinaryTest;
import org.iii.ufo.shdep.nodes.test.ParenTest;
import org.iii.ufo.shdep.nodes.test.UnaryTest;

// The class override necessary node for traversal
public class NodeVisitorAdapter implements NodeVisitor{
	
	private static void accept(Node node, NodeVisitor visitor){
		if(node != null) node.accept(visitor);
	}
	// =======================================================

	@Override
	public void visit(ScriptFile script) {
		script.getStmtList().forEach(stmt->stmt.accept(this));
	}

	@Override
	public void visit(StmtList stmts) {
		stmts.forEach(stmt->stmt.accept(this));
	}

	@Override
	public void visit(Stmt stmt) {
		accept(stmt.getCmd(), this);
		stmt.getRedirs().forEach(redir->redir.accept(this));
	}
	
	@Override
	public void visit(Redirect redir) {
		accept(redir.getWord(), this);
		accept(redir.getHdoc(), this);
	}
	
	@Override
	public void visit(Op op) {
		//nothing
	}

	@Override
	public void visit(Assign assign){
		accept(assign.getValue(), this);
	}

	//Word Part ==========================
	@Override
	public void visit(Lit lit) {
		// nothing
	}

	@Override
	public void visit(ParamExp exp) {
		// nothing
	}

	@Override
	public void visit(SglQuoted sq) {
		// nothing
	}

	@Override
	public void visit(DblQuoted quote){
		quote.getWord().accept(this);
	}

	@Override
	public void visit(CmdSubst subst){
		subst.getStmts().forEach(stmt->stmt.accept(this));
	}

	@Override
	public void visit(ArithmExp exp){
		accept(exp.getX(), this);
	}

	@Override
	public void visit(ExtGlob extGlob) {
		//do nothing
	}
	
	// Command =================================

	@Override
	public void visit(CallExpr cmd) {
		cmd.getAssigns().forEach(assign->assign.accept(this));
		cmd.getArgs().forEach(arg->arg.accept(this));
	}

	@Override
	public void visit(BinaryCmd cmd) {
		cmd.getX().accept(this);
		cmd.getY().accept(this);
	}

	@Override
	public void visit(FuncDecl cmd) {
		cmd.getBody().accept(this);
	}

	@Override
	public void visit(Block cmd) {
		cmd.getStmtList().forEach(stmt->stmt.accept(this));
	}

	@Override
	public void visit(IfClause cmd) {
		cmd.getCond().forEach(stmt->stmt.accept(this));
		cmd.getThen().forEach(stmt->stmt.accept(this));
		cmd.getElse().forEach(stmt->stmt.accept(this));
	}

	@Override
	public void visit(CaseClause cmd) {
		cmd.getItems().forEach(item->item.accept(this));
	}
	
	@Override
	public void visit(CaseItem item) {
		item.getStmtList().forEach(stmt->stmt.accept(this));
	}
	
	@Override
	public void visit(WhileClause cmd){
		cmd.getCond().forEach(stmt->stmt.accept(this));
		cmd.getDo().forEach(stmt->stmt.accept(this));
	}
	
	@Override
	public void visit(DeclClause decl){
		decl.getOpts().forEach(opt->opt.accept(this));
		decl.getAssigns().forEach(assign->assign.accept(this));
	}

	@Override
	public void visit(Subshell cmd){
		cmd.getStmtList().forEach(stmt->stmt.accept(this));
	}

	@Override
	public void visit(ForClause cmd){
		cmd.getLoop().accept(this);
		cmd.getDo().forEach(stmt->stmt.accept(this));
	}

	@Override
	public void visit(LetClause clause) {
		clause.getExprs().forEach(expr->expr.accept(this));
	}

	@Override
	public void visit(TestClause clause) {
		clause.getX().accept(this);
	}
	//Arithm ============================================

	@Override
	public void visit(Word word){
		if(word != null){
			word.getParts().forEach(part->part.accept(this));
		}
	}

	@Override
	public void visit(BinaryArithm arithm) {
		arithm.getX().accept(this);
		arithm.getY().accept(this);
	}

	@Override
	public void visit(ParenArithm arithm) {
		arithm.getX().accept(this);
	}

	@Override
	public void visit(UnaryArithm arithm) {
		arithm.getX().accept(this);
	}
	
	// Loop ===================================
	@Override
	public void visit(WordIter iter) {
		iter.getItems().forEach(word->word.accept(this));
	}

	//TestExpr ====================

	@Override
	public void visit(BinaryTest test) {
		test.getX().accept(this);
		test.getY().accept(this);
	}

	@Override
	public void visit(UnaryTest test) {
		test.getX().accept(this);
	}

	@Override
	public void visit(ParenTest test) {
		test.getX().accept(this);
	}

}
