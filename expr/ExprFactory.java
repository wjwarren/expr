package expr;

/**
 * Static factory for creating the four types of expressions possible.
 */
final class ExprFactory {
	private ExprFactory() {}
	/**
	 * Make a literal expression.
	 * 
	 * @param v
	 *            the constant value of the expression
	 * @return an expression whose value is always v
	 */
	static Expr makeLiteral(double v) {
		return new LiteralExpr(v);
	}

	/**
	 * Make an expression that applies a unary operator to an operand.
	 * 
	 * @param rator
	 *            a code for a unary operator
	 * @param rand
	 *            operand
	 * @return an expression meaning rator(rand)
	 */
	static Expr makeApp1(int rator, Expr rand) {
		Expr app = new UnaryExpr(rator, rand);
		return rand instanceof LiteralExpr ? new LiteralExpr(app.value()) : app;
	}

	/**
	 * Make an expression that applies a binary operator to two operands.
	 * 
	 * @param rator
	 *            a code for a binary operator
	 * @param rand0
	 *            left operand
	 * @param rand1
	 *            right operand
	 * @return an expression meaning rator(rand0, rand1)
	 */
	static Expr makeApp2(int rator, Expr rand0, Expr rand1) {
		Expr app = new BinaryExpr(rator, rand0, rand1);
		return rand0 instanceof LiteralExpr && rand1 instanceof LiteralExpr ? new LiteralExpr(
				app.value())
				: app;
	}

	/**
	 * Make a conditional expression.
	 * 
	 * @param test
	 *            `if' part
	 * @param consequent
	 *            `then' part
	 * @param alternative
	 *            `else' part
	 * @return an expression meaning `if test, then consequent, else
	 *         alternative'
	 */
	static Expr makeIfThenElse(Expr test, Expr consequent,
			Expr alternative) {
		Expr cond = new ConditionalExpr(test, consequent, alternative);
		if (test instanceof LiteralExpr)
			return test.value() != 0 ? consequent : alternative;
		else
			return cond;
	}
}

final class LiteralExpr implements Expr {
	final double v;

	LiteralExpr(double v) {
		this.v = v;
	}

	@Override
	public final double value() {
		return v;
	}
}

final class UnaryExpr implements Expr {
	final int rator;
	final Expr rand;

	UnaryExpr(int rator, Expr rand) {
		this.rator = rator;
		this.rand = rand;
	}

	@Override
	public double value() {
		final double arg = rand.value();
		switch (rator) {
		case ExprConstants.ABS:
			return Math.abs(arg);
		case ExprConstants.ACOS:
			return Math.acos(arg);
		case ExprConstants.ASIN:
			return Math.asin(arg);
		case ExprConstants.ATAN:
			return Math.atan(arg);
		case ExprConstants.CEIL:
			return Math.ceil(arg);
		case ExprConstants.COS:
			return Math.cos(arg);
		case ExprConstants.EXP:
			return Math.exp(arg);
		case ExprConstants.FLOOR:
			return Math.floor(arg);
		case ExprConstants.LOG:
			return Math.log(arg);
		case ExprConstants.NEG:
			return -arg;
		case ExprConstants.ROUND:
			return Math.rint(arg);
		case ExprConstants.SIN:
			return Math.sin(arg);
		case ExprConstants.SQRT:
			return Math.sqrt(arg);
		case ExprConstants.TAN:
			return Math.tan(arg);
		default:
			throw new RuntimeException("BUG: bad rator");
		}
	}
}

final class BinaryExpr implements Expr {
	final int rator;
	final Expr rand0, rand1;

	BinaryExpr(int rator, Expr rand0, Expr rand1) {
		this.rator = rator;
		this.rand0 = rand0;
		this.rand1 = rand1;
	}

	@Override
	public double value() {
		final double arg0 = rand0.value();
		final double arg1 = rand1.value();
		switch (rator) {
		case ExprConstants.ADD:
			return arg0 + arg1;
		case ExprConstants.SUB:
			return arg0 - arg1;
		case ExprConstants.MUL:
			return arg0 * arg1;
		case ExprConstants.DIV:
			return arg0 / arg1; // division by 0 has IEEE 754 behavior
		case ExprConstants.POW:
			return Math.pow(arg0, arg1);
		case ExprConstants.ATAN2:
			return Math.atan2(arg0, arg1);
		case ExprConstants.MAX:
			return arg0 < arg1 ? arg1 : arg0;
		case ExprConstants.MIN:
			return arg0 < arg1 ? arg0 : arg1;
		case ExprConstants.LT:
			return arg0 < arg1 ? 1.0 : 0.0;
		case ExprConstants.LE:
			return arg0 <= arg1 ? 1.0 : 0.0;
		case ExprConstants.EQ:
			return arg0 == arg1 ? 1.0 : 0.0;
		case ExprConstants.NE:
			return arg0 != arg1 ? 1.0 : 0.0;
		case ExprConstants.GE:
			return arg0 >= arg1 ? 1.0 : 0.0;
		case ExprConstants.GT:
			return arg0 > arg1 ? 1.0 : 0.0;
		case ExprConstants.AND:
			return arg0 != 0 && arg1 != 0 ? 1.0 : 0.0;
		case ExprConstants.OR:
			return arg0 != 0 || arg1 != 0 ? 1.0 : 0.0;
		default:
			throw new RuntimeException("BUG: bad rator");
		}
	}
}

final class ConditionalExpr implements Expr {
	final Expr test, consequent, alternative;

	ConditionalExpr(Expr test, Expr consequent, Expr alternative) {
		this.test = test;
		this.consequent = consequent;
		this.alternative = alternative;
	}

	@Override
	public double value() {
		return test.value() != 0 ? consequent.value() : alternative.value();
	}
}
