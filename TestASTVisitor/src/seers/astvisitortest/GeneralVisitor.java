package seers.astvisitortest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * General visitor that extracts methods and fields of a Java compilation unit
 *
 */
public class GeneralVisitor extends ASTVisitor {

	/**
	 * List of methods
	 */
	private List<String> methods;
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

		// add the name of the method to the list
		SimpleName name = node.getName();
		methods.add(name.getFullyQualifiedName());

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

	public List<String> getMethods() {
		return methods;
	}

	public List<String> getFields() {
		return fields;
	}

}
