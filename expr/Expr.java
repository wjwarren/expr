// Mathematical expressions.
// Copyright 1996 by Darius Bacon; see the file COPYING.

package expr;

/**
 * A mathematical expression, built out of literal numbers, variables,
 * arithmetic and relational operators, and elementary functions. It can be
 * evaluated to get its value given its variables' current values. The operator
 * names are from java.lang.Math where possible.
 */
public interface Expr {

    /**
     * Calculate the expression's value.
     * 
     * @return the value given the current variable values
     */
    public double value();
}
