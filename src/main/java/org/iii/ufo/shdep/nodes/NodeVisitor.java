package org.iii.ufo.shdep.nodes;

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

public interface NodeVisitor {

	public void visit(Assign assing);
	public void visit(CaseItem item);
	public void visit(Op op);
	public void visit(ScriptFile script);
	public void visit(StmtList stmts);
	public void visit(Stmt stmt);
	public void visit(Redirect redir);
	// Word part ======================
	public void visit(CmdSubst subst);
	public void visit(SglQuoted sq);
	public void visit(DblQuoted dq);
	public void visit(Lit lit);
	public void visit(ParamExp exp);
	public void visit(ArithmExp exp);
	public void visit(ExtGlob extGlob);
	//command ===================
	public void visit(CallExpr cmd);
	public void visit(BinaryCmd cmd);
	public void visit(FuncDecl cmd);
	public void visit(Block cmd);
	public void visit(IfClause cmd);
	public void visit(CaseClause cmd);
	public void visit(WhileClause cmd);
	public void visit(DeclClause decl);
	public void visit(Subshell subshell);
	public void visit(ForClause forClause);
	public void visit(LetClause letClause);
	//Arithm =====================
	public void visit(BinaryArithm arithm);
	public void visit(UnaryArithm unaryArithm);
	public void visit(ParenArithm parenArithm);
	public void visit(Word word);
	//Loop ========================
	public void visit(WordIter wordIter);
	//TestExpr ====================
	public void visit(BinaryTest binaryTest);
	public void visit(UnaryTest unaryTest);
	public void visit(ParenTest parenTest);
	public void visit(TestClause testClause);
}
