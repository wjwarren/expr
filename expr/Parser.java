// Operator-precedence parser.
// Copyright 1996 by Darius Bacon; see the file COPYING.

package expr;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Parses strings representing mathematical formulas with variables. The
 * following operators, in descending order of precedence, are defined:
 * 
 * <UL>
 * <LI>^ (raise to a power)
 * <LI>/
 * <LI>Unary minus (-x)
 * <LI>+ -
 * <LI>&lt; &lt;= = &lt;&gt; &gt;= &gt;
 * <LI>and
 * <LI>or
 * </UL>
 * 
 * ^ associates right-to-left; other operators associate left-to-right.
 * 
 * <P>
 * These unary functions are defined: abs, acos, asin, atan, ceil, cos, exp,
 * floor, log, round, sin, sqrt, tan. Each requires one argument enclosed in
 * parentheses.
 * 
 * <P>
 * There are also binary functions: atan2, min, max; and a ternary conditional
 * function: if(test, then, else).
 * 
 * <P>
 * Whitespace outside identifiers is ignored.
 * 
 * <P>
 * The constant 'pi' is built in and equal to the value returned by Math.PI
 * 
 * <P>
 * Examples:
 * <UL>
 * <LI>42
 * <LI>2-3
 * <LI>cos(x^2) + sin(x^2)
 * <UL>
 */
public class Parser {
	
	/*
	 * This must be here in order to initialize ExprConstants by the time
	 * this class loads.
	 */
	static {
		ExprConstants.pi.setValue(Math.PI);
	}
	
	/**
	 * Set of Variable's that are allowed to appear in input expressions. If
	 * null, any variable is allowed.
	 */
	private Hashtable<Variable, Variable> allowedVariables = null;

	/**
	 * Adjust the set of allowed variables: create it (if not yet existent) and
	 * add optVariable (if it's nonnull). If the allowed-variable set exists,
	 * the parser will reject input strings that use any other variables.
	 * 
	 * @param optVariable
	 *            the variable to be allowed, or null
	 */
	public void allow(Variable optVariable) {
		if (null == allowedVariables) {
			allowedVariables = new Hashtable<Variable, Variable>();
			allowedVariables.put(ExprConstants.pi, ExprConstants.pi);
		}
		if (null != optVariable)
			allowedVariables.put(optVariable, optVariable);
	}

	/** Scanner for input left (deliminated by {@code operatorChars}). */
	private Scanner tokens = null;
	
	/** Currently processed token **/
	private Token token = null;

	/**
	 * Return the expression denoted by the input string.
	 * 
	 * @param input
	 *            the unparsed expression
	 * @exception SyntaxException
	 *                if the input is unparsable
	 */
	public Expr parseString(String input) throws SyntaxException {
		tokens = new Scanner(input, operatorChars);
		return reparse();
	}

	/** Special characters which may separate tokens */
	static private final String operatorChars = "*/+-^<>=,()";

	private Expr reparse() throws SyntaxException {
		tokens.index = -1;
		nextToken();
		Expr expr = parseExpr(0);
		if (token.ttype != Token.TT_EOF)
			throw error("Incomplete expression", SyntaxException.INCOMPLETE,
					null);
		return expr;
	}

	private void nextToken() {
		token = tokens.nextToken();
	}

	private Expr parseExpr(int precedence) throws SyntaxException {
		Expr expr = parseFactor();
		loop: for (;;) {
			int l, r, rator;

			// The operator precedence table.
			// l = left precedence, r = right precedence, rator = operator.
			// Higher precedence values mean tighter binding of arguments.
			// To associate left-to-right, let r = l+1;
			// to associate right-to-left, let r = l.

			switch (token.ttype) {

			case '<':
				l = 20;
				r = 21;
				rator = ExprConstants.LT;
				break;
			case Token.TT_LE:
				l = 20;
				r = 21;
				rator = ExprConstants.LE;
				break;
			case '=':
				l = 20;
				r = 21;
				rator = ExprConstants.EQ;
				break;
			case Token.TT_NE:
				l = 20;
				r = 21;
				rator = ExprConstants.NE;
				break;
			case Token.TT_GE:
				l = 20;
				r = 21;
				rator = ExprConstants.GE;
				break;
			case '>':
				l = 20;
				r = 21;
				rator = ExprConstants.GT;
				break;

			case '+':
				l = 30;
				r = 31;
				rator = ExprConstants.ADD;
				break;
			case '-':
				l = 30;
				r = 31;
				rator = ExprConstants.SUB;
				break;

			case '/':
				l = 40;
				r = 41;
				rator = ExprConstants.DIV;
				break;
			case '*':
				l = 40;
				r = 41;
				rator = ExprConstants.MUL;
				break;

			case '^':
				l = 50;
				r = 50;
				rator = ExprConstants.POW;
				break;

			default:
				if (token.ttype == Token.TT_WORD && token.sval.equals("and")) {
					l = 5;
					r = 6;
					rator = ExprConstants.AND;
					break;
				}
				if (token.ttype == Token.TT_WORD && token.sval.equals("or")) {
					l = 10;
					r = 11;
					rator = ExprConstants.OR;
					break;
				}
				break loop;
			}

			if (l < precedence)
				break loop;

			nextToken();
			expr = ExprFactory.makeApp2(rator, expr, parseExpr(r));
		}
		return expr;
	}

	/** names of unary operators */
	static private final String[] procs1 = { "abs", "acos", "asin", "atan",
			"ceil", "cos", "exp", "floor", "log", "round", "sin", "sqrt", "tan" };
	/** list of int codes in same order as @{code procs1} */
	static private final int[] rators1 = { ExprConstants.ABS, ExprConstants.ACOS, ExprConstants.ASIN,
		ExprConstants.ATAN, ExprConstants.CEIL, ExprConstants.COS, ExprConstants.EXP, ExprConstants.FLOOR, ExprConstants.LOG,
		ExprConstants.ROUND, ExprConstants.SIN, ExprConstants.SQRT, ExprConstants.TAN };

	/** names of binary operators */
	static private final String[] procs2 = { "atan2", "max", "min" };
	/** list of int codes in same order as @{code procs2} */
	static private final int[] rators2 = { ExprConstants.ATAN2, ExprConstants.MAX, ExprConstants.MIN };

	private Expr parseFactor() throws SyntaxException {
		switch (token.ttype) {
		case Token.TT_NUMBER: {
			Expr lit = ExprFactory.makeLiteral(token.nval);
			nextToken();
			return lit;
		}
		case Token.TT_WORD: {
			for (int i = 0; i < procs1.length; ++i)
				if (procs1[i].equals(token.sval)) {
					nextToken();
					expect('(');
					Expr rand = parseExpr(0);
					expect(')');
					return ExprFactory.makeApp1(rators1[i], rand);
				}

			for (int i = 0; i < procs2.length; ++i)
				if (procs2[i].equals(token.sval)) {
					nextToken();
					expect('(');
					Expr rand1 = parseExpr(0);
					expect(',');
					Expr rand2 = parseExpr(0);
					expect(')');
					return ExprFactory.makeApp2(rators2[i], rand1, rand2);
				}

			if (token.sval.equals("if")) {
				nextToken();
				expect('(');
				Expr test = parseExpr(0);
				expect(',');
				Expr consequent = parseExpr(0);
				expect(',');
				Expr alternative = parseExpr(0);
				expect(')');
				return ExprFactory.makeIfThenElse(test, consequent, alternative);
			}

			Expr var = VariableFactory.make(token.sval);
			if (null != allowedVariables && null == allowedVariables.get(var))
				throw error("Unknown variable",
						SyntaxException.UNKNOWN_VARIABLE, null);
			nextToken();
			return var;
		}
		case '(': {
			nextToken();
			Expr enclosed = parseExpr(0);
			expect(')');
			return enclosed;
		}
		case '-':
			nextToken();
			return ExprFactory.makeApp1(ExprConstants.NEG, parseExpr(35));
		case Token.TT_EOF:
			throw error("Expected a factor", SyntaxException.PREMATURE_EOF,
					null);
		default:
			throw error("Expected a factor", SyntaxException.BAD_FACTOR, null);
		}
	}

	private SyntaxException error(String complaint, int reason, String expected) {
		return new SyntaxException(complaint, this, reason, tokens, expected);
	}

	private void expect(int ttype) throws SyntaxException {
		if (token.ttype != ttype)
			throw error("'" + (char) ttype + "' expected",
					SyntaxException.EXPECTED, "" + (char) ttype);
		nextToken();
	}

	// Error correction

	boolean tryCorrections() {
		return tryInsertions() || tryDeletions() || trySubstitutions();
	}

	private boolean tryInsertions() {
		Vector<Token> v = tokens.tokens;
		for (int i = tokens.index; 0 <= i; --i) {
			Token t;
			if (i < v.size()) {
				t = v.elementAt(i);
			} else {
				String s = tokens.getInput();
				t = new Token(Token.TT_EOF, 0, s, s.length(), s.length());
			}
			Token[] candidates = possibleInsertions(t);
			for (int j = 0; j < candidates.length; ++j) {
				v.insertElementAt(candidates[j], i);
				try {
					reparse();
					return true;
				} catch (SyntaxException se) {
					v.removeElementAt(i);
				}
			}
		}
		return false;
	}

	private boolean tryDeletions() {
		Vector<Token> v = tokens.tokens;
		for (int i = tokens.index; 0 <= i; --i) {
			if (v.size() <= i)
				continue;
			Token t = v.elementAt(i);
			v.remove(i);
			try {
				reparse();
				return true;
			} catch (SyntaxException se) {
				v.insertElementAt(t, i);
			}
		}
		return false;
	}

	private boolean trySubstitutions() {
		Vector<Token> v = tokens.tokens;
		for (int i = tokens.index; 0 <= i; --i) {
			if (v.size() <= i)
				continue;
			Token t = v.elementAt(i);
			Token[] candidates = possibleSubstitutions(t);
			for (int j = 0; j < candidates.length; ++j) {
				v.setElementAt(candidates[j], i);
				try {
					reparse();
					return true;
				} catch (SyntaxException se) {
				}
			}
			v.setElementAt(t, i);
		}
		return false;
	}

	private Token[] possibleInsertions(Token t) {
		Token[] ts = new Token[operatorChars.length() + 6 + procs1.length
				+ procs2.length];
		int i = 0;

		Token one = new Token(Token.TT_NUMBER, 1, "1", t);
		ts[i++] = one;

		for (int j = 0; j < operatorChars.length(); ++j) {
			char c = operatorChars.charAt(j);
			ts[i++] = new Token(c, 0, Character.toString(c), t);
		}

		ts[i++] = new Token(Token.TT_WORD, 0, "x", t);

		for (int k = 0; k < procs1.length; ++k)
			ts[i++] = new Token(Token.TT_WORD, 0, procs1[k], t);

		for (int m = 0; m < procs2.length; ++m)
			ts[i++] = new Token(Token.TT_WORD, 0, procs2[m], t);

		ts[i++] = new Token(Token.TT_LE, 0, "<=", t);
		ts[i++] = new Token(Token.TT_NE, 0, "<>", t);
		ts[i++] = new Token(Token.TT_GE, 0, ">=", t);
		ts[i++] = new Token(Token.TT_WORD, 0, "if", t);

		return ts;
	}

	private Token[] possibleSubstitutions(Token t) {
		return possibleInsertions(t);
	}
}
