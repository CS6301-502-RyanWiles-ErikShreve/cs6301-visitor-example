package edu.utdallas.cs6301_502;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.Statement;

/**
 * Visitor for Identifying Unused Fields and Variables in Java Source Code
 *
 */
public class UnusedVisitor extends ASTVisitor {

	
	public class VariableStrings
	{
		public String name;
		public String type;
	}
	
	public class MethodStrings
	{
		public String name;
		public List<VariableStrings> parameters;
		public List<VariableStrings> variables;
		
		
		public MethodStrings() {
			super();
			name = "";
			parameters = new ArrayList<VariableStrings>();
			variables = new ArrayList<VariableStrings>();
		}
	}
	
	/**
	 * List of methods
	 */
	private List<MethodStrings> methods;
	/**
	 * List of fields
	 */
	private List<String> fields;

	boolean inMethod = false;
	
	
	/**
	 * Default constructor
	 */
	public UnusedVisitor() {
		methods = new ArrayList<>();
		fields = new ArrayList<>();
	}

	
	
	
	/**
	 * Assignments are made within methods.
	 * Assignments do not include the Initializer of VariableDeclrationFragments.
	 */
	@Override
	public boolean visit(Assignment node) {
		
		System.out.println(node.toString());

		return super.visit(node);
	}


	@Override
	public boolean visit(VariableDeclarationFragment node) {
		System.out.println(node.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		inMethod = false;
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
