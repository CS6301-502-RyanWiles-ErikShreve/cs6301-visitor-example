package edu.utdallas.cs6301_502;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Visitor for Identifying Unused Fields and Variables in Java Source Code
 *
 * Design Notes:
 * Fields and Vars are set to values in VariableDeclarationFragment and Assignment nodes
 * FieldDeclaration for field declarations
 * SingleVariableDeclaration for parameter declarations (but these have no initializer)
 * VariableDeclarationFragment is used for field declarations, 
 *   local var declarations, for statement initializers, and lamda expression parameters.
 *
 * The Expression class has several subclasses of interest, such as ArrayInitializer, Assignment, etc...
 *
 * Obtaining the IVariableBinding on names will likely be useful. The IVariableBinding contains
 * getKey(), isField(), and isParameter(). The key returned by getKey() is unique for a given AST.
 *
 */
public class UnusedVisitor extends ASTVisitor {

	


	private boolean inMethod = false;
	private boolean inRHS = false;
	private boolean inMethodInvocation = false;
	
	public HashMap<String, UnusedItem> varRead;
	
	private boolean debug = false;
	
	public class UnusedItem
	{
		String type;
		String name;
		int lineNumber;
		boolean wasRead;
		
		public UnusedItem(String type, String name, int lineNumber) {
			super();
			this.type = type;
			this.name = name;
			this.lineNumber = lineNumber;
			this.wasRead = false;
		}
		
		
	}
	
	/**
	 * Default constructor
	 */
	public UnusedVisitor() {
		varRead = new HashMap<>();
	}

	public UnusedVisitor(boolean debug) {
		varRead = new HashMap<>();
		this.debug = debug;
	}
	
	private void debug(String msg)
	{
		if (debug)	System.out.println(msg);
	}
	
	/**
	 * Assignments are made within methods.
	 * Assignments do not include the Initializer of VariableDeclrationFragments.
	 */
	@Override
	public boolean visit(Assignment node) {
		
	
		inRHS = true;
		node.getRightHandSide().accept(this);
		inRHS = false;
		
		
		return false;
	}

	

	@Override
	public boolean visit(MethodInvocation node) {

		inMethodInvocation = true;

		Expression e1 = node.getExpression();
		
		if (e1 != null) {
			e1.accept(this);
			debug(e1.toString());
		}
		
		@SuppressWarnings("unchecked")
		List<Expression> expressions = node.arguments();
		for (Expression e : expressions)
		{
			e.accept(this);
		}
		inMethodInvocation = false;
		return false;
	}




	@Override
	public boolean visit(SimpleName node) {
		
		if (inRHS || inMethodInvocation)
		{
			debug("SimpleName: Key: " + node.resolveBinding().getKey());
			if (varRead.containsKey(node.resolveBinding().getKey()))
			{
				UnusedItem ui = varRead.get(node.resolveBinding().getKey());
				ui.wasRead = true;				
				varRead.put(node.resolveBinding().getKey(), ui);
			}
		}
		
		return super.visit(node);
	}



	// TODO: This won't handle the case where a field is used before it is declared.
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		
		IVariableBinding binding = (IVariableBinding) node.getName().resolveBinding();
		
		// TODO: Is inMethod needed?
		if (inMethod || binding.isField())
		{
			CompilationUnit cu = (CompilationUnit) node.getRoot();
			
			String type = "variable";
			if (binding.isField())
			{
				type = "field";
			}
			
			
			UnusedItem ui = new UnusedItem(type, 
					node.getName().getFullyQualifiedName(), 
					cu.getLineNumber(node.getStartPosition()));
			varRead.put(node.getName().resolveBinding().getKey(), ui);			
		}	
		
		return true;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		inMethod = false;
		inRHS = false;
		inMethodInvocation = false;
		return super.visit(node);
	}


	@Override
	public void endVisit(MethodDeclaration node) {
		inMethod = false;
		super.endVisit(node);
	}


	@Override
	public boolean visit(MethodDeclaration node) {
		inMethod = true;
		return super.visit(node);
	}

}
