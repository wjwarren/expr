// Variables associate values with names.
// Copyright 1996 by Darius Bacon; see the file COPYING.

package expr;

/**
 * A variable is a simple expression with a name (like "x") and a settable
 * value.
 */
public final class Variable implements Expr {
	private String name;
	private double val;

	/**
	 * Create a new variable, with initial value 0.
	 * 
	 * @param name
	 *            the variable's name
	 */
	public Variable(String name) {
		this.name = name;
		val = 0;
	}

	/** Return the name. */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Get the value.
	 * 
	 * @return the current value
	 */
	@Override
	public double value() {
		return val;
	}

	/**
	 * Set the value.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(double value) {
		val = value;
	}
}
