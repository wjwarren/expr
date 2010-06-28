package expr;

import java.util.Hashtable;

/**
 * Generates Variable objects, ensuring that only one object is created
 * for each variable.
 */
public class VariableFactory {
	private VariableFactory() {}
	private static Hashtable<String, Variable> variables = new Hashtable<String, Variable>();

	/**
	 * Return a unique variable named `name'. There can be only one variable
	 * with the same name returned by this method; that is, make(s1) == make(s2)
	 * if and only if s1.equals(s2).
	 * 
	 * @param name
	 *            the variable's name
	 * @return the variable; create it initialized to 0 if it doesn't yet exist
	 */
	static public synchronized Variable make(String name) {
		Variable result = variables.get(name);
		if (result == null)
			variables.put(name, result = new VariableObj(name));
		return result;
	}
}

final class VariableObj implements Variable {
	private String name;
	private double val;

	/**
	 * Create a new variable, with initial value 0.
	 * 
	 * @param name
	 *            the variable's name
	 */
	public VariableObj(String name) {
		this.name = name;
		val = 0;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public double value() {
		return val;
	}

	@Override
	public void setValue(double value) {
		val = value;
	}
}