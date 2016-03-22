// ***************************************************************************
// Assignment: 4
// Team : 1
// Team Members: Ryan Wiles, Erik Shreve
//
// Code reuse/attribution notes:
// args4j (for command line parsing) based on example code from:
// https://github.com/kohsuke/args4j/blob/master/args4j/examples/SampleMain.java
//
// parseFile and setParserConfiguration taken from/based on seers.astvisitortest.MainVisitor
// ***************************************************************************
package edu.utdallas.cs6301_502;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import edu.utdallas.cs6301_502.UnusedVisitor.VarFieldInfo;


class Runner {
	@Option(name = "-d", usage = "print debug information to console")
	private boolean debug = false;

	// Default is relative to project location. 
	@Option(name = "-s", usage = "Source (java file or directory)")
	private String source = ".." + File.separator + "SampleCode" + File.separator + "Countdown.java";
	

	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<String>();

	
	public static void main(String... args) throws Exception {
		Runner r = new Runner();
		r.doMain(args);
		r.run();
	}

	public void doMain(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the arguments.
			parser.parseArgument(args);

		} catch (CmdLineException e) {
			// report an error message.
			System.err.println(e.getMessage());
			System.err.println("java Runner [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();

			return;
		}
	}

	public Runner() {
		super();
	}

	public void run() {
		try {
			File file = new File(source);
			
			CompilationUnit compUnit = parseFile(file);

			// create and accept the visitor
			UnusedVisitor visitor = new UnusedVisitor();
			visitor.setDebug(debug);
			compUnit.accept(visitor);
			
			System.out.println("File: " + source);
		
			for (String k : visitor.varRead.keySet())
			{
				if (!visitor.varRead.get(k).wasRead)
				{
					VarFieldInfo info = visitor.varRead.get(k);
					if (info.varBinding != null)
					{
						String type = "variable";
						if (info.varBinding.isField()) {type = "field";}
						System.out.println("* The [" + type + "] ["+ info.varBinding.getName() + "] is declared but never read in the code (line:[" + info.lineNumber + "])");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Parses a java file
	 * 
	 * @param file
	 *            the file to parse
	 * @return the CompilationUnit of a java file (i.e., its AST)
	 * @throws IOException
	 */
	private static CompilationUnit parseFile(File file) throws IOException {

		// read the content of the file
		char[] fileContent = FileUtils.readFileToString(file).toCharArray();

		// create the AST parser
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setUnitName(file.getName());
		parser.setSource(fileContent);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// set some default configuration
		setParserConfiguration(parser);

		// parse and return the AST
		return (CompilationUnit) parser.createAST(null);

	}

	/**
	 * Sets the default configuration of an AST parser
	 * 
	 * @param parser
	 *            the AST parser
	 */
	public static void setParserConfiguration(ASTParser parser) {
		@SuppressWarnings("unchecked")
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);

		parser.setCompilerOptions(options);
		parser.setResolveBindings(true);

		parser.setEnvironment(null, null, null, true);
		//parser.setEnvironment(classPaths, sourceFolders, encodings, true);
	}
	
}