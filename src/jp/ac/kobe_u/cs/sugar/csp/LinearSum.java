package jp.ac.kobe_u.cs.sugar.csp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.kobe_u.cs.sugar.SugarConstants;
import jp.ac.kobe_u.cs.sugar.SugarException;
import jp.ac.kobe_u.cs.sugar.expression.Expression;

/**
 * A class for linear expressions.
 * A linear expression represents the following formula:<br>
 * a0*x0+a1*x1+...+an*xn+b<br>
 * where ai's and b are integer constants and xi's are integer variables
 * of CSP.
 * @see CSP
 * @see LinearLiteral
 * @author Naoyuki Tamura
 */
public class LinearSum {
	private int b;
	private SortedMap<IntegerVariable,Integer> coef;
	private IntegerDomain domain = null;

	public LinearSum(int b) {
		coef = new TreeMap<IntegerVariable,Integer>();
		this.b = b;
	}

	public LinearSum(int a0, IntegerVariable v0, int b) {
		this(b);
		coef.put(v0, a0);
	}

	public LinearSum(IntegerVariable v0) {
		this(1, v0, 0);
	}

	public LinearSum(LinearSum e) {
		b = e.b;
		coef = new TreeMap<IntegerVariable,Integer>(e.coef);
		domain = null;
	}

	/**
	 * Returns the size of the linear expression. 
	 * @return the size
	 */
	public int size() {
		return coef.size();
	}
	
	public int getB() {
		return b;
	}
	
	public SortedMap<IntegerVariable,Integer> getCoef() {
		return coef;
	}

	public Set<IntegerVariable> getVariables() {
		return coef.keySet();
	}

	public boolean isIntegerVariable() {
		return b == 0 && size() == 1 && getA(coef.firstKey()) == 1;  
	}
	
	/**
	 * Returns true when the linear expression is simple.
	 * A linear expression is simple if it has at most one integer variable.
	 * @return true when the linear expression is simple
	 */
	public boolean isSimple() {
		return coef.size() <= 1;
	}

	public Integer getA(IntegerVariable v) {
		Integer a = coef.get(v);
		if (a == null) {
			a = 0;
		}
		return a;
	}
	
	public void setA(int a, IntegerVariable v) {
		if (a == 0) {
			coef.remove(v);
		} else {
			coef.put(v, a);
		}
		domain = null;
	}

	public boolean isDomainLargerThan(long limit) {
		long size = 1;
		for (IntegerVariable v : coef.keySet()) {
			size *= v.getDomain().size();
			if (size > limit)
				return true;
		}
		return false;
	}
	
	/**
	 * Adds the given linear expression.
	 * @param linearSum the linear expression to be added.
	 */
	public void add(LinearSum linearSum) {
		b += linearSum.b;
		for (IntegerVariable v : linearSum.coef.keySet()) {
			int a = getA(v) + linearSum.getA(v);
			setA(a, v);
		}
		domain = null;
	}

	/**
	 * Subtracts the given linear expression.
	 * @param linearSum the linear expression to be subtracted.
	 */
	public void subtract(LinearSum linearSum) {
		b -= linearSum.b;
		for (IntegerVariable v : linearSum.coef.keySet()) {
			int a = getA(v) - linearSum.getA(v);
			setA(a, v);
		}
		domain = null;
	}

	/**
	 * Multiplies the given constant.
	 * @param c the constant to be multiplied by
	 */
	public void multiply(int c) {
		b *= c;
		for (IntegerVariable v : coef.keySet()) {
			int a = c * getA(v);
			setA(a, v);
		}
		domain = null;
	}

	public void divide(int c) {
		b /= c;
		for (IntegerVariable v : coef.keySet()) {
			int a = getA(v) / c;
			setA(a, v);
		}
		domain = null;
	}

	private int gcd(int p, int q) {
		while (true) {
			int r = p % q;
			if (r == 0)
				break;
			p = q;
			q = r;
		}
		return q;
	}
	
	public int factor() {
		if (size() == 0) {
			return b == 0 ? 1 : Math.abs(b);
		}
		int gcd = Math.abs(getA(coef.firstKey()));
		for (IntegerVariable v : coef.keySet()) {
			gcd = gcd(gcd, Math.abs(getA(v)));
			if (gcd == 1)
				break;
		}
		if (b != 0) {
			gcd = gcd(gcd, Math.abs(b));
		}
		return gcd;
	}

	public void factorize() {
		int factor = factor();
		if (factor > 1) {
			divide(factor);
		}
	}
	
	public IntegerDomain getDomain() throws SugarException {
		if (domain == null) {
			domain = new IntegerDomain(b, b);
			for (IntegerVariable v : coef.keySet()) {
				int a = getA(v);
				domain = domain.add(v.getDomain().mul(a));
			}
		}
		return domain;
	}
	
	public IntegerDomain getDomainExcept(IntegerVariable v) throws SugarException {
		// Re-calculation is needed since variable domains might be modified. 
		IntegerDomain d = new IntegerDomain(b, b);
		for (IntegerVariable v1 : coef.keySet()) {
			if (! v1.equals(v)) {
				int a = getA(v1);
				d = d.add(v1.getDomain().mul(a));
			}
		}
		return d;
	}

	public LinearSum[] split() {
		LinearSum e1 = new LinearSum(b);
		LinearSum e2 = new LinearSum(0);
		int i = 0;
		for (IntegerVariable v : coef.keySet()) {
			if (i % 2 == 0) {
				e1.setA(getA(v), v);
			} else {
				e2.setA(getA(v), v);
			}
			i++;
		}
		return new LinearSum[] { e1, e2 };
	}

	public IntegerVariable[] getVariablesByCoef() {
		int n = coef.size();
		IntegerVariable[] vs = new IntegerVariable[n];
		vs = coef.keySet().toArray(vs);
		Arrays.sort(vs, new Comparator<IntegerVariable>() {
			public int compare(IntegerVariable v1, IntegerVariable v2) {
				int a1 = Math.abs(getA(v1));
				int a2 = Math.abs(getA(v2));
				if (a1 != a2)
					return a1 > a2 ? -1 : 1;
				return v1.compareTo(v2);
			}
		});
		return vs;
	}
	
	public Expression toExpression() {
		Expression x = Expression.create(b);
		for (IntegerVariable v : coef.keySet()) {
			Expression ax = Expression.create(getA(v));
			Expression vx = Expression.create(v.getName());
			x = x.add(ax.mul(vx));
		}
		return x;
	}
	
	/**
	 * Returns the value of the linear expression.
	 * @return the value of the linear expression
	 */
	public int getValue() {
		int value = b;
		for (IntegerVariable v : coef.keySet()) {
			value += getA(v) * v.getValue();
		}
		return value;
	}

	/**
	 * Returns true when the linear expression is equal to the given linear expression.
	 * @param linearSum the linear expression to be compared
	 * @return true when the linear expression is equal to the given linear expression
	 */
	public boolean equals(LinearSum linearSum) {
		if (linearSum == null)
			return false;
		if (this == linearSum)
			return true;
		return b == linearSum.b && coef.equals(linearSum.coef);
	}
	
	/**
	 * Returns true when the linear expression is equal to the given object.
	 * @param obj the object to be compared
	 * @return true when the linear expression is equal to the given object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return equals((LinearSum)obj);
	}

	/**
	 * Returns the hash code of the linear expression.
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((coef == null) ? 0 : coef.hashCode());
		result = PRIME * result + b;
		return result;
	}

	/**
	 * Returns the string representation of the linear expression.
	 * @return the string representation
	 */
	public String toString0() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(SugarConstants.ADD);
		for (IntegerVariable v : coef.keySet()) {
			sb.append(" (");
			sb.append(SugarConstants.MUL);
			sb.append(" ");
			sb.append(coef.get(v));
			sb.append(" ");
			sb.append(v.getName());
			sb.append(")");
		}
		sb.append(" ");
		sb.append(b);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IntegerVariable v : coef.keySet()) {
			int c = getA(v);
			if (c == 0) {
			} else if (c > 0) {
				if (sb.length() > 0) {
					sb.append("+");
				}
				if (c != 1) {
					sb.append(c);
					sb.append("*");
				}
				sb.append(v.getName());
			} else {
				if (c == -1) {
					sb.append("-");
				} else {
					sb.append(c);
					sb.append("*");
				}
				sb.append(v.getName());
			}
		}
		if (sb.length() == 0) {
			sb.append(b);
		} else {
			if (b >= 0) {
				sb.append("+");
			}
			sb.append(b);
		}
		return sb.toString();
	}

}
