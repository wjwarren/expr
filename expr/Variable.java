// Variables associate values with names.
// Copyright 1996 by Darius Bacon; see the file COPYING.

package expr;

/**
 * A variable is a simple expression with a name (like "x") and a settable
 * value. These are created with the {@code VariableFactory.make} method.
 * 
 * @see VariableFactory
 */
public interface Variable extends Expr {

    /**
     * Set the value.
     * 
     * @param value
     *            the new value
     */
    public void setValue(double value);
}
