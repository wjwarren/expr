package expr;

/**
 * Static factory for creating the four types of expressions possible.
 */
final class ExprFactory {
	private ExprFactory() {
	}

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
		if (rator == ExprConstants.POW && (rand1 instanceof LiteralExpr)
				&& Math.floor(rand1.value()) == rand1.value())
			return new PolynomialExpr(rand0, rator, rand1);
		if (rator == ExprConstants.MUL && (rand0 instanceof LiteralExpr)
				&& (Math.floor(rand0.value()) == rand0.value()))
			return new PolynomialExpr(rand0, rator, rand1);
		if ((rator == ExprConstants.ADD || rator == ExprConstants.SUB)
				&& (rand0 instanceof LiteralExpr || rand0 instanceof PolynomialExpr)
				&& (rand1 instanceof LiteralExpr || rand1 instanceof PolynomialExpr))
			if (rand0 instanceof PolynomialExpr
					&& rand1 instanceof PolynomialExpr
					&& ((PolynomialExpr) rand0).var != ((PolynomialExpr) rand1).var) {
				// we can't combine polynomials of different variables
			} else
				return new PolynomialExpr(rand0, rator, rand1);
		if ((rator == ExprConstants.ADD || rator == ExprConstants.SUB)
				&& ((rand0 instanceof PolynomialExpr && ((PolynomialExpr) rand0).var == rand1) || (rand1 instanceof PolynomialExpr && ((PolynomialExpr) rand1).var == rand0)))
			return new PolynomialExpr(rand0,rator,rand1);
		Expr app = new BinaryExpr(rator, rand0, rand1);
		return rand0 instanceof LiteralExpr && rand1 instanceof LiteralExpr ? new LiteralExpr(
				app.value()) : app;
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
	static Expr makeIfThenElse(Expr test, Expr consequent, Expr alternative) {
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

final class PolynomialExpr implements Expr {
	final double[] coeff;
	final Expr var;

	PolynomialExpr(Expr a, int rator, Expr b) {
		switch (rator) {
		case ExprConstants.POW:
			final int exponent = (int) b.value();
			coeff = new double[exponent + 1];
			coeff[exponent] = 1;
			var = a;
			break;
		case ExprConstants.MUL:
			final double factor = a.value();

			if (!(b instanceof PolynomialExpr)) {
				coeff = new double[2];
				coeff[1] = factor;
				var = b;
				return;
			}
			coeff = ((PolynomialExpr) b).coeff;
			for (int i = 0; i < coeff.length; ++i) {
				coeff[i] *= factor;
			}
			var = ((PolynomialExpr) b).var;
			break;
		case ExprConstants.ADD:
		case ExprConstants.SUB:
			if (a instanceof LiteralExpr && b instanceof LiteralExpr) {
				coeff = new double[1];
				coeff[0] = rator == ExprConstants.ADD ? a.value() + b.value()
						: a.value() - b.value();
				var = null;
			} else if (a instanceof PolynomialExpr
					&& b instanceof PolynomialExpr) {
				double[] bigger, smaller;
				double[] alist = ((PolynomialExpr) a).coeff;
				double[] blist = ((PolynomialExpr) b).coeff;
				if (alist.length > blist.length) {
					bigger = alist;
					smaller = blist;
				} else {
					bigger = blist;
					smaller = alist;
				}
				coeff = bigger;
				for (int i = 0; i < smaller.length; ++i) {
					coeff[i] = rator == ExprConstants.ADD ? alist[i] + blist[i]
							: alist[i] - blist[i];
				}
				var = ((PolynomialExpr) a).var;
			} else {
				// one is polynomial, other is literal or variable
				if (a instanceof PolynomialExpr) {
					coeff = ((PolynomialExpr) a).coeff;
					var = ((PolynomialExpr) a).var;
					if(b instanceof LiteralExpr)
						coeff[0] += rator == ExprConstants.ADD ? b.value() : -b
								.value();
					else
						coeff[1] += rator == ExprConstants.ADD ? 1 : -1;
				} else {
					coeff = ((PolynomialExpr) b).coeff;
					var = ((PolynomialExpr) b).var;
					if (rator == ExprConstants.SUB) {
						for (int i = 0; i < coeff.length; ++i)
							coeff[i] *= -1;
					}
					if(a instanceof LiteralExpr)
						coeff[0] += a.value();
					else
						coeff[1] += 1;
				}
			}
			break;
		default:
			coeff = null;
			var = null;
		}
	}

	public int terms() {
		int i = 0;
		for (int j = 0; j < coeff.length; ++j)
			if (coeff[j] != 0)
				i++;
		return i;
	}

	/**
	 * To avoid summary execution, using this algorithm from Numerical Recipies
	 * (1992 version in C)
	 */
	@Override
	public double value() {
		if (coeff.length == 1)
			return coeff[0];
		final double variable = var.value();
		double p = coeff[coeff.length - 1];
		for (int j = coeff.length - 2; j >= 0; j--)
			p = p * variable + coeff[j];
		return p;
	}
}
