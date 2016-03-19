package seers.astvisitortest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.Statement;

/**
 * General visitor that extracts methods and fields of a Java compilation unit
 *
 */
public class GeneralVisitor extends ASTVisitor {

	
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

	/**
	 * Default constructor
	 */
	public GeneralVisitor() {
		methods = new ArrayList<>();
		fields = new ArrayList<>();
	}

	/**
	 * Method that visits the method declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	@Override
	public boolean visit(MethodDeclaration node) {

		MethodStrings ms = new MethodStrings();
		
		// add the name of the method to the list

		ms.name = node.getName().getFullyQualifiedName();
		
		methods.add(ms);

		@SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> parameters = node.parameters();
		for (SingleVariableDeclaration p : parameters)
		{
			VariableStrings vs = new VariableStrings();
			
			vs.name = p.getName().getFullyQualifiedName();
			vs.type = p.getType().toString();
			// The toString doesn't indicate if the parameter is a vararg.
			if (p.isVarargs())
			{
				vs.type += "[]";
			}
			ms.parameters.add(vs);
		}
		
		@SuppressWarnings("unchecked")
		List<Statement> statements = node.getBody().statements();
		for (Statement s : statements)
		{
			if (s instanceof VariableDeclarationStatement)
			{
				VariableDeclarationStatement vds = (VariableDeclarationStatement) s;
				@SuppressWarnings("unchecked")
				List<VariableDeclarationFragment> fragments = vds.fragments();
				
				for (VariableDeclarationFragment fragment : fragments)
				{
					VariableStrings vs = new VariableStrings();
					vs.name = fragment.getName().getFullyQualifiedName();
					vs.type = vds.getType().toString();
					
					ms.variables.add(vs);
				}
			}
		}
		
		return super.visit(node);
	}

	/**
	 * Method that visits the field declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldDeclaration)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		// get the fragments of the field declaration
		List<VariableDeclarationFragment> varFragments = node.fragments();
		for (VariableDeclarationFragment fragment : varFragments) {

			// add the name of the field
			fields.add(fragment.getName().getFullyQualifiedName());
		}
		return super.visit(node);
	}

	public List<MethodStrings> getMethods() {
		return methods;
	}

	public List<String> getFields() {
		return fields;
	}

}
